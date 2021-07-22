package br.edu.ifmg.sdtrab.controller;


import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.TransactionDao;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;


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

    public void transaction(User u){
        System.out.println("transaction");
        // TODO(lucasgb): Verificações

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.ASYNC();

            HashMap<String, String> hs = new HashMap();
            hs.put("tipo", "NEW");
            hs.put("usuario", u.getName());

            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
            }
            else {

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void transfer(User u1, User u2, float value){
        System.out.println("transaction");
        // TODO(lucasgb): Verificações

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.ASYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", "NEW");
            hs.put("usuario1", u1);
            hs.put("usuario2", u2);
            hs.put("value", value);
            var list = dispatcher.castMessage(null, new ObjectMessage(null, hs), options);
            if (list == null) {
            }
            else {

            }
        }
        catch (Exception e) {
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

        } else if (msgF.get("tipo").equals("TRANSACTIONS")) {

        }
        return null;
    }

    // Processar requisição assíncrona
    @Override
    public void handle(Message msg, Response response) throws Exception {
        HashMap msgF = msg.getObject();
        if (msgF.get("tipo").equals("TRANSFER")) {

        } else if (msgF.get("tipo").equals("TRANSACTIONS")) {

        }
    }
}
