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
        lockService = new LockService(channel);
        address = channel.getAddress();
    }

    public void close() {
        Util.close(dispatcher, channel);
    }

    public User newUser(String name, String password) {
        System.out.println("newUser");
        // TODO(lucasgb): Verificações

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_ALL);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, String> hs = new HashMap();
            hs.put("tipo", "NEW");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
                return null;
            }
            else {
                User u = new User();
                u.setName(name);
                u.setPasswordHash(password);
                userDao.save(u);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean authUser(String name, String password) {
        System.out.println("newUser");
        // TODO(lucasgb): Verificações

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, String> hs = new HashMap();
            hs.put("tipo", "NEW");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
                return false;
            }
            else {

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Mensagem recebida
    @Override
    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    // Mudança na estrutura de clientes conectados
    public void viewAccepted(View new_view) {
        //System.out.println("** view: " + new_view);
    }

    // Processar requisição síncrona
    @Override
    public Object handle(Message msg) throws Exception {
        HashMap msgF = msg.getObject();
        if (msgF.get("tipo").equals("NEW")) {
            System.out.println(msgF.get("tipo"));
            System.out.println(msgF.get("usuario") + " " + msgF.get("senha"));
        }
        else if (msgF.get("tipo").equals("LOGIN")) {
            HashMap<String, String> msgL = msg.getObject();
            String user = msgL.get("usuario");
            String password = msgL.get("senha");
            authUser(user, password);
        }
        else if (msgF.get("tipo").equals("BALACE")) {

        }
        else {

        }
        return null;
    }

    // Processar requisição assíncrona
    @Override
    public void handle(Message msg, Response response) throws Exception {
        HashMap msgF = msg.getObject();
        if (msgF.get("tipo").equals("NEW")) {
            System.out.println(msgF.get("tipo"));
            System.out.println(msgF.get("usuario") + " " + msgF.get("senha"));
            response.send("Sucess", false);
        }
        else if (msgF.get("tipo").equals("LOGIN")) {
            HashMap<String, String> msgL = msg.getObject();
            String user = msgL.get("usuario");
            String password = msgL.get("senha");
            authUser(user, password);
        }
        else if (msgF.get("tipo").equals("BALACE")) {

        }
        else {

        }
    }
}
