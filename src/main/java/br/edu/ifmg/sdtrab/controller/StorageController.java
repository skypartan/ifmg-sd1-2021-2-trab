package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.TransactionDao;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    private TransactionSqliteDao transactionDao;
    private UserSqliteDao userDao;

    public StorageController() {

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
                if (list.getResults().contains(3)) {
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
        //System.out.println("transfer");
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

    public void initRole() throws Exception{
        channel = new JChannel(new ProtocolUtil().channelProtocols());
        channel.setReceiver(this);
        channel.connect("ebankData");
        dispatcher = new MessageDispatcher(channel, this);
        lockService = new LockService(channel);
        address = channel.getAddress();
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
    public void getState(OutputStream output) throws Exception {

    }

    @Override
    public void setState(InputStream input) throws Exception {

    }

    @Override
    public Object handle(Message msg) throws Exception {
        HashMap<String, Object> msgF = msg.getObject();
        Transaction transaction = new Transaction();

        if (msgF.get("tipo").equals("TRANSFER")) {
            String u2 = (String) msgF.get("usuario2");
            User u1 = (User) msgF.get("usuario1");

            User user1 = userDao.find(u1.getId(), this.channel.getAddressAsString());
            User user2 = userDao.find(u2, this.channel.getAddressAsString());

            float value = (float) msgF.get("value");
            float val1 = user1.getBalance().floatValue();
            float val2 = user2.getBalance().floatValue();

            user1.setBalance(BigDecimal.valueOf(val2-value));
            user2.setBalance(BigDecimal.valueOf(val1+value));

            transaction.setSender(user1);
            transaction.setReceiver(user2);
            transaction.setValue(BigDecimal.valueOf(value));
            transaction.setTime(new Timestamp(System.currentTimeMillis()));


            if (user1 == null) {
                return 1;
            } else if (userDao.find(user2.getId(), msg.getSrc().toString()) == null) {
                return 1;
            } else if ((user1.getBalance().floatValue() - value) < 0) {
                return 2;
            } else {
                userDao.update(user1, this.channel.getAddressAsString());
                userDao.update(user2,this.channel.getAddressAsString());
                transactionDao.save(transaction, this.channel.getAddressAsString());
                return 0;
            }
        } else if (msgF.get("tipo").equals("TRANSACTIONS")) {
            User u = (User) msgF.get("usuario");
            HashMap<String, ArrayList<Transaction>> transfer = new HashMap();
            transfer.put("Recebidos", transactionDao.findbyReceiverId(u.getId(), msg.getSrc().toString()));
            transfer.put("Enviados", transactionDao.findbySenderId(u.getId(), msg.getSrc().toString()));
            return transfer;
        }
        return 3;
    }
}
