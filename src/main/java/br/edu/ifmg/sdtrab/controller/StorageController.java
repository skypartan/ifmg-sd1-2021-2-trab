package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.Connector;
import br.edu.ifmg.sdtrab.storage.TransactionSqliteDao;
import br.edu.ifmg.sdtrab.storage.UserDao;
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
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

public class StorageController implements RequestHandler, Receiver {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;

    public StorageController(NodeController nodeController) {

    }

    public void connect() throws Exception {
        Protocol[] p = ProtocolUtil.channelProtocols();
        channel = new JChannel(p);
        channel.setReceiver(this);
        channel.connect("ebankData");
        dispatcher = new MessageDispatcher(channel, this);
        lockService = new LockService(channel);
        if (this.networkSize() > 1) {
            Lock lock = lockService.getLock("lockState"); // gets a cluster-wide lock
            lock.lock();
            try {
                this.getState();
            } finally {
                lock.unlock();
            }
        }
        RATE_LIMITER rate = (RATE_LIMITER) p[14];
        rate.setMaxBytes(200);
        rate.setTimePeriod(1000);
        address = channel.getAddress();
    }

    public void disconnect() throws Exception {

    }

    public ObjectMessage controllerHandle(ObjectMessage message) {
        return null;
    }

    public int networkSize() {
        return channel.getView().getMembers().size();
    }

    public Object transaction(User u) {
        // TODO(lucasgb): Verificações
        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "TRANSACTIONS");
            hs.put("usuario", u);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
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

    public String transfer(User u1, String u2, float value) {
        // TODO(lucasgb): Verificações
        try {
            // User user2 = userDao.find(u2, channel.getAddressAsString());
            try {
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
                    var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
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

    public Object newUser(String name, String password) {
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

    public User authUser(String name, String password) {
        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            RequestOptions.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "LOGIN");
            hs.put("usuario", name);
            hs.put("senha", password);
            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
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

    public BigDecimal balance(String name, String password) {
        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "BALANCE");
            hs.put("usuario", name);
            hs.put("senha", password);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
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

    protected void getState() {
        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "GET_OBJECT");
            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
                int x;
            } else {
                var status = (HashMap) list.getFirst();
                var user = (ArrayList<User>) status.get("user");
                var transaction = (ArrayList<Transaction>) status.get("transf");

                new Connector().dropDatabase(this.channel.getAddressAsString());

                UserSqliteDao ud = new UserSqliteDao();
                TransactionSqliteDao td = new TransactionSqliteDao();

                for (int i = 0; i < user.size(); i++) {
                    ud.save(user.get(i), this.channel.getAddressAsString());
                }

                for (int i = 0; i < transaction.size(); i++) {
                    td.save(transaction.get(i), this.channel.getAddressAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Mensagem recebida
    @Override
    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    // Mudança na estrutura de clientes conectados
    public void viewAccepted(View new_view) {
        System.out.println("** view transaction: " + new_view);
    }


    @Override
    public Object handle(Message msg) throws Exception {
        TransactionSqliteDao transactionDao = new TransactionSqliteDao();
        UserSqliteDao userDao = new UserSqliteDao();
        var action = (HashMap<String, Object>) msg.getObject();
        var tipo = (String) action.get("tipo");
        switch (tipo) {
            case "TRANSFER":
                Transaction transaction = new Transaction();
                String u2 = (String) action.get("usuario2");
                User u1 = (User) action.get("usuario1");

                User user1 = userDao.find(u1.getId(), this.channel.getAddressAsString());
                User user2 = userDao.find(u2, this.channel.getAddressAsString());

                float value = (float) action.get("value");
                float val1 = user1.getBalance().floatValue();
                float val2 = user2.getBalance().floatValue();

                user1.setBalance(BigDecimal.valueOf(val2 - value));
                user2.setBalance(BigDecimal.valueOf(val1 + value));

                transaction.setSender(user1);
                transaction.setReceiver(user2);
                transaction.setValue(BigDecimal.valueOf(value));
                transaction.setTime(new Timestamp(System.currentTimeMillis()));


                if (user1 == null) {
                    return 1;
                } else if (userDao.find(user2.getId(), this.channel.getAddressAsString()) == null) {
                    return 1;
                } else if ((user1.getBalance().floatValue() - value) < 0) {
                    return 2;
                } else {
                    userDao.update(user1, this.channel.getAddressAsString());
                    userDao.update(user2, this.channel.getAddressAsString());
                    transactionDao.save(transaction, this.channel.getAddressAsString());
                    return 0;
                }
            case "TRANSACTIONS":
                User u = (User) action.get("usuario");
                HashMap<String, ArrayList<Transaction>> transfer = new HashMap();
                transfer.put("Recebidos", transactionDao.findbyReceiverId(u.getId(), this.channel.getAddressAsString()));
                transfer.put("Enviados", transactionDao.findbySenderId(u.getId(), this.channel.getAddressAsString()));
                return transfer;

            case "NEW":
                var usuario = userDao.find((String) action.get("usuario"), this.channel.getAddressAsString());
                if (usuario != null)
                    return "ERROR usuário já existe";
                User newUser = new User();
                newUser.setName((String) action.get("usuario"));
                newUser.setPasswordHash((String) action.get("senha"));
                newUser.setBalance(BigDecimal.valueOf(1000));
                userDao.save(newUser, this.channel.getAddressAsString());
                newUser = userDao.find(newUser.getName(), this.channel.getAddressAsString());
                return newUser;
            case "LOGIN":
                var senha = (String) action.get("senha");
                var name = (String) action.get("usuario");
                var usuario1 = userDao.find(name, this.channel.getAddressAsString());
                if (usuario1 != null) {
                    if (!usuario1.getPasswordHash().equals(senha))
                        return "AUTH falha senha incorreta";
                    else
                        return userDao.find(name, this.channel.getAddressAsString());
                } else
                    return "AUTH falha usuário não encontrado";
            case "BALANCE":
                var usuario3 = userDao.find((String) action.get("usuario"), this.channel.getAddressAsString());
                if (usuario3 != null) {
                    return String.format("ACCOUNT %f", usuario3.getBalance());
                } else
                    return "ACCOUNT falha usuário não encontrado";
            case "GET_OBJECT":
                HashMap<String, ArrayList> getObject = new HashMap<>();
                getObject.put("user", userDao.selectAll(this.channel.getAddressAsString()));
                getObject.put("transf", transactionDao.selectAll(this.channel.getAddressAsString()));
                return getObject;
            default:
                // do nothing
                return 3;
        }
    }
}
