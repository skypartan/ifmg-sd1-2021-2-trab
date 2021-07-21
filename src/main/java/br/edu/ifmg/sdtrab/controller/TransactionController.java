package br.edu.ifmg.sdtrab.controller;


import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;
import org.jgroups.util.MessageBatch;
import org.jgroups.util.Util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;


public class TransactionController  implements RequestHandler, Receiver {
    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    LockService lock_service;


    public TransactionController() {
    }

    public void init() throws Exception {
        channel = new JChannel(new Protocols().channelProtocols());
        channel.setReceiver(this);
        channel.connect("ebankTransaction");
        dispatcher = new MessageDispatcher(channel, this);
        lock_service = new LockService(channel);
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
