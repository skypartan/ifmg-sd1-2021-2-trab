package br.edu.ifmg.sdtrab.controller;


import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.TransactionDao;
import br.edu.ifmg.sdtrab.storage.UserDao;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;


public class TransactionController implements RequestHandler, Receiver {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;
    private TransactionDao transactionDao;

    public TransactionController() {
    }

    public void init() throws Exception {
        channel = new JChannel(new Protocols().channelProtocols());
        channel.setReceiver(this);
        channel.connect("ebankTransaction");
        dispatcher = new MessageDispatcher(channel, this);
        lockService = new LockService(channel);
        address = channel.getAddress();
    }

    public void transaction(User u) {
        System.out.println("transaction");
        // TODO(lucasgb): Verificações

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.ASYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "TRANSACTIONS");
            hs.put("usuario", u);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list != null) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transfer(User u1, User u2, float value) {
        System.out.println("transfer");
        // TODO(lucasgb): Verificações
        LockService lock_service = new LockService(channel);
        try {
            UserDao userDao = new UserDao();
            TransactionDao transactionDao = new TransactionDao();
            Transaction transaction = new Transaction();

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_ALL);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "TRANSFER");
            hs.put("usuario1", u1);
            hs.put("usuario2", u2);
            hs.put("value", value);
            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list != null) {
                Lock lock = lock_service.getLock("mylock"); // gets a cluster-wide lock
                lock.lock();
                try {
                    //userDao.updateBalance(u1.getBalance().floatValue() - value);
                    //userDao.updateBalance(u2.getBalance().floatValue() + value);
                    transactionDao.save(transaction);
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
        //System.out.println("** view: " + new_view);
    }

    // Processar requisição síncrona
    @Override
    public Object handle(Message msg) throws Exception {
        HashMap<String, Object> msgF = msg.getObject();
        if (msgF.get("tipo").equals("TRANSFER")) {
            User u1 = (User) msgF.get("usuario1");
            User u2 = (User) msgF.get("usuario2");

            float value = (float) msgF.get("value");
            UserDao userDao = new UserDao();
            User user = userDao.find(u1.getId());
            if (user == null) {
                return "Erro: Usurio não existente";
            } else if (userDao.find(u2.getId()) == null) {
                return "Erro: Usurio não existente";
            } else if ((user.getBalance().floatValue() - value) < 0) {
                return "Erro: Dinheiro insuficiente";
            } else {
                return "Sucesso";
            }
        } else if (msgF.get("tipo").equals("TRANSACTIONS")) {
            User u = (User) msgF.get("usuario");
            UserDao userDao = new UserDao();
            User user = userDao.find(u.getId());
            TransactionDao transactionDao = new TransactionDao();
            if (user == null) {
                return "Erro: Usurio não existente";
            } else {
                HashMap<String, Object> transfer = new HashMap();
                transfer.put("Recebidos", transactionDao.findbyReceiverId(user.getId()));
                transfer.put("Enviados", transactionDao.findbySenderId(user.getId()));
                return transfer;
            }
        }
        return "Erro: Operação não encontrada";
    }

    // Processar requisição assíncrona
    @Override
    public void handle(Message msg, Response response) throws Exception {
        HashMap<String, Object> msgF = msg.getObject();
        if (msgF.get("tipo").equals("TRANSFER")) {
            User u1 = (User) msgF.get("usuario1");
            User u2 = (User) msgF.get("usuario2");

            float value = (float) msgF.get("value");
            UserDao userDao = new UserDao();
            User user = userDao.find(u1.getId());
            if (user == null) {
                response.send("Erro: Usurio não existente", true);
            } else if (userDao.find(u2.getId()) == null) {
                response.send("Erro: Usurio não existente", true);
            } else if ((user.getBalance().floatValue() - value) < 0) {
                response.send("Erro: Dinheiro insuficiente", true);
            } else {
                response.send("Sucesso", false);
            }
        } else if (msgF.get("tipo").equals("TRANSACTIONS")) {
            User u = (User) msgF.get("usuario");
            UserDao userDao = new UserDao();
            User user = userDao.find(u.getId());
            TransactionDao transactionDao = new TransactionDao();
            if (user == null) {
                response.send("Erro: Usurio não existente", true);
            } else {
                HashMap<String, Object> transfer = new HashMap();
                transfer.put("Recebidos", transactionDao.findbyReceiverId(user.getId()));
                transfer.put("Enviados", transactionDao.findbySenderId(user.getId()));
                response.send(transfer, false);
            }
        }
        response.send("Erro: Operação não encontrada", true);
    }
}
