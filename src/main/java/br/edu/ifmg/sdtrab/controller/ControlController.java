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
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.Lock;

public class ControlController extends ReceiverAdapter implements RequestHandler {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;
    private NodeController nodeController;
    private int count = -1;

    public ControlController(NodeController nodeController) {
        this.nodeController = nodeController;
    }

    public void connect() throws Exception {
        if (channel != null && channel.isConnected())
            return;

        Protocol[] p = ProtocolUtil.channelProtocols();
        channel = new JChannel(p);
        channel.setReceiver(this);
        dispatcher = new MessageDispatcher(channel, this, this, this);
        System.out.println("Conectando a ebankControl");
        channel.connect("ebankControl");
        lockService = new LockService(channel);
        address = channel.getAddress();
    }

    public void disconnect() {
        if (channel != null && channel.isConnected())
            channel.disconnect();
    }

    public Message controllerHandle(Message message) {
        System.out.println("Controlador de controle, recebido " + message);

        if (count == channel.getView().getMembers().size()) {
            count = 0;
        }
        count++;

        var destino = channel.getView().getMembers().get(channel.getView().getMembers().size() - 1);

        var action = (HashMap<String, Object>) message.getObject();
        var tipo = (String) action.get("tipo");
        switch (tipo) {
            case "TRANSFER":
                return new Message(null, transfer(destino,
                        (User) action.get("usuario1"), (String) action.get("usuario2"), (Float) action.get("value")));
            case "TRANSACTIONS":
                return new Message(null,
                        transaction(destino,
                                (User) action.get("usuario")));
            case "NEW":
                System.out.println("Criando novo usuário");
                return new Message(null, newUser(destino,
                        (String) action.get("usuario"), (String) action.get("senha")));
            case "LOGIN":
                return new Message(null, authUser(destino,
                        (String) action.get("usuario"), (String) action.get("senha")));
            case "BALANCE":
                new Message(null, balance(destino,
                        (String) action.get("usuario"), (String) action.get("senha")));
            case "SUM_MONEY":
                return new Message(null,
                        sum_money(destino));
            default:
                // do nothing
                return null;
        }
    }

    public int networkSize() {
        if (channel.isConnected()) {
            System.out.println("Nós no canal de controle: " + Arrays.toString(channel.getView().getMembers().toArray()));
            return channel.getView().getMembers().size();
        }

        System.out.println("Não conectado no canal de controle");
        return 1;
    }

    public BigDecimal sum_money(Address destino) {
        try {
            var ls = new ArrayList<Address>();
            ls.add(destino);
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "SUM_MONEY");


            var list = dispatcher.castMessage(ls, new Message(destino, hs), options);
            if (list == null) {
                return null;
            } else {
                var status = (BigDecimal) list.getFirst();
                return status;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

            var list = dispatcher.castMessage(ls, new Message(destino, hs), options);
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
                    var list = dispatcher.castMessage(ls, new Message(destino, hs), options);
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
        System.out.println("newUser");

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

            System.out.println("Enviando mensagem para nó de controle");
            var list = dispatcher.sendMessage(new Message(destino, hs), options);
            System.out.println("Recebido " + list);
            if (list == null) {
                return null;
            } else {
                if (list instanceof String) {
                    if (((String) list).contains("ERROR usuário já existe")) {
                        return "Usuário já existe";
                    }
                }
                else {
                    return list;
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
            var list = dispatcher.castMessage(ls, new Message(destino, hs), options);
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

            var list = dispatcher.castMessage(ls, new Message(destino, hs), options);
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

    @Override
    public void receive(Message msg) {

    }

    @Override
    public void viewAccepted(View newView) {

    }

    @Override
    public Object handle(Message msg) throws Exception {
        System.out.println("Nó de controle, recebido " + msg);

        var action = (HashMap<String, Object>) msg.getObject();
        var src = nodeController.getDirectoryService().storageController();
        action.put("task", "storage");
        return nodeController.getDirectoryService().sendMessage(new Message(src, action));
    }
}
