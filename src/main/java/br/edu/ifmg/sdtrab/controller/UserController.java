package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.UserDao;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;
import org.jgroups.util.Util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class UserController implements RequestHandler, Receiver {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;
    private UserDao userDao;

    public UserController() {

    }

    public void init() throws Exception {
        channel = new JChannel(new Protocols().channelProtocols());
        channel.setReceiver(this);
        channel.connect("ebankUser");
        dispatcher = new MessageDispatcher(channel, this);
        //lockService = new LockService(channel);
        address = channel.getAddress();

        userDao = new UserDao();
    }

    public void close() {
        Util.close(dispatcher, channel);
    }

    public User newUser(String name, String password) {
        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_ALL);
            options.setAnycasting(false);
            RequestOptions.SYNC();

            HashMap<String, String> hs = new HashMap<>();
            hs.put("tipo", "NEW");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
                return null;
            }
            else {
                var status = (String) list.getFirst();
                if (status.startsWith("FREE")) {
                    User u = new User();
                    u.setName(name);
                    u.setPasswordHash(password);
                    userDao.save(u);
                }
                else {
                    System.out.println("Usuário já existe");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public User authUser(String name, String password) {
        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "LOGIN");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
                return null;
            }
            else {
                var status = (String) list.getFirst();
                if (status.startsWith("AUTH falha")) {
                    return null;
                }
                else if (status.startsWith("AUTH sucesso")) {
                    return userDao.find(name);
                }
                else {
                    return null;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Mensagem recebida
    @Override
    public void receive(Message msg) {
        //System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    // Mudança na estrutura de clientes conectados
    public void viewAccepted(View new_view) {
        //System.out.println("** view: " + new_view);
    }

    // Processar requisição síncrona
    @Override
    public Object handle(Message msg) throws Exception {
        var action = (HashMap<String, Object>) msg.getObject();
        var tipo = (String)  action.get("tipo");
        var usuario = userDao.find((String) action.get("usuario"));

        switch (tipo) {
            case "NEW":
                if (usuario != null)
                    return "ERROR usuário já existe";
                return "FREE";
            case "LOGIN":
                if (usuario != null) {
                    var senha = (String) action.get("senha");
                    if (!usuario.getPasswordHash().equals(senha))
                        return "AUTH falha senha incorreta";
                    else
                        return "AUTH sucesso";
                }
                else
                    return "AUTH falha usuário não encontrado";
            case "BALANCE":
                if (usuario != null) {
                    return String.format("ACCOUNT %f", usuario.getBalance());
                }
                else
                    return "AUTH falha usuário não encontrado";
            default:
                // do nothing
                return null;
        }
    }

    // Processar requisição assíncrona
    @Override
    public void handle(Message msg, Response response) throws Exception {
        response.send(handle(msg), false);
    }
}
