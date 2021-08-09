package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.TransactionSqliteDao;
import br.edu.ifmg.sdtrab.storage.UserSqliteDao;
import br.edu.ifmg.sdtrab.util.ProtocolUtil;
import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.RATE_LIMITER;
import org.jgroups.stack.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;

public class ControlController implements RequestHandler, Receiver {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;

    public ControlController(NodeController nodeController) {

    }

    public void connect() {

    }

    public void disconnect() {

    }

    public ObjectMessage controllerHandle(ObjectMessage message) {
        return null;
    }

    public ObjectMessage nodeHandle(ObjectMessage message) {
        return null;
    }

    public int networkSize() {
        return channel.getView().getMembers().size();
    }

    public Object transaction(Address destino, User u) {
        // TODO(lucasgb): Verificações
        try {
            var ls = new ArrayList<Address>();
            ls.add(destino);
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "TRANSACTIONS");
            hs.put("usuario", u);

            var list = dispatcher.castMessage(ls, new ObjectMessage(destino, hs), options);
            if (list != null) {
                var status = (Integer) list.getFirst();
                if (status == 3) {
                    return null;
                } else {
                    return list.getResults();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public String transfer(Address destino, User u1, String u2, float value) {
        //System.out.println("transfer");
        // TODO(lucasgb): Verificações
        try {
            // User user2 = userDao.find(u2, channel.getAddressAsString());
            try {
                var ls = new ArrayList<Address>();
                ls.add(destino);
                var options = new RequestOptions();
                options.setMode(ResponseMode.GET_ALL);
                options.setAnycasting(false);
                options.SYNC();

                HashMap<String, Object> hs = new HashMap<>();
                hs.put("tipo", "TRANSFER");
                hs.put("usuario1", u1);
                hs.put("usuario2", u2);
                hs.put("value", value);
                Lock lock = lockService.getLock("lockTrans"); // gets a cluster-wide lock
                lock.lock();
                try {
                    var list = dispatcher.castMessage(ls, new ObjectMessage(destino, hs), options);
                    if (list != null) {

                        if (list.getResults().contains(1)) {
                            return "Erro: Usuario não encotrado";
                        } else if (list.getResults().contains(2)) {
                            return "Erro: Dinheiro insuficiente";
                        } else if (list.getResults().contains(3)) {
                            return "Erro: Operação inexitente";
                        } else {
                            return "Traferencia Realizada com Sucesso";
                        }

                    }
                } finally {
                    lock.unlock();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object newUser(Address destino, String name, String password) {
        try {
            var ls = new ArrayList<Address>();
            ls.add(destino);
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            RequestOptions.SYNC();

            HashMap<String, String> hs = new HashMap<>();
            hs.put("tipo", "NEW");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(ls, new ObjectMessage(destino, hs), options);
            if (list == null) {
                return null;
            } else {
                var status = list.getResults();
                if (status.contains("ERROR usuário já existe")) {
                    return "Usuário já existe";
                } else {
                    return status.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public User authUser(Address destino, String name, String password) {
        try {
            var ls = new ArrayList<Address>();
            ls.add(destino);
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            RequestOptions.SYNC();

            HashMap<String, Object> hs = new HashMap<>();
            hs.put("tipo", "LOGIN");
            hs.put("usuario", name);
            hs.put("senha", password);
            var list = dispatcher.castMessage(ls, new ObjectMessage(destino, hs), options);
            if (list == null) {
                return null;
            } else {
                var status = (Object) list.getFirst();
                if (status.toString().startsWith("AUTH falha")) {
                    return null;
                } else if (status.equals(3)) {
                    return null;
                } else {
                    return (User) status;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public BigDecimal balance(Address destino, String name, String password) {
        try {
            var ls = new ArrayList<Address>();
            ls.add(destino);
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap<>();
            hs.put("tipo", "BALANCE");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(ls, new ObjectMessage(destino, hs), options);
            if (list == null) {
                return null;
            } else {
                var status = (String) list.getFirst();
                if (status.startsWith("ACCOUNT falha")) {
                    return null;
                } else {
                    return BigDecimal.valueOf(Double.parseDouble(status.replace("ACCOUNT ", "").replace(",", ".")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void initRole() throws Exception {
        new ProtocolUtil();
        Protocol[] p = ProtocolUtil.channelProtocols();
        channel = new JChannel(p);
        channel.setReceiver(this);
        channel.connect("ebankData");
        dispatcher = new MessageDispatcher(channel, this);
        lockService = new LockService(channel);
        RATE_LIMITER rate = (RATE_LIMITER) p[14];
        rate.setMaxBytes(200);
        rate.setTimePeriod(1000);
        address = channel.getAddress();
    }

    @Override
    public void receive(Message msg) {

    }

    @Override
    public void viewAccepted(View newView) {

    }

    @Override
    public Object handle(Message msg) throws Exception {
        var action = (HashMap<String, Object>) msg.getObject();
        var tipo = (String) action.get("tipo");
        switch (tipo) {
            case "TRANSFER":
                break;
            case "TRANSACTIONS":
                break;
            case "NEW":
                break;
            case "LOGIN":
                break;
            case "BALANCE":
                break;
            default:
                // do nothing
                return 3;
        }
        return 3;
    }
}
