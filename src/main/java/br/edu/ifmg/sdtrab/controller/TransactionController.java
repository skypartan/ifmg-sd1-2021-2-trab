package br.edu.ifmg.sdtrab.controller;


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


public class TransactionController implements RequestHandler, Receiver {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;


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
