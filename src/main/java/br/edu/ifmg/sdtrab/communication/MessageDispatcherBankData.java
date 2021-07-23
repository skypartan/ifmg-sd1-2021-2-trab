package br.edu.ifmg.sdtrab.communication;

import br.edu.ifmg.sdtrab.controller.Protocols;
import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

import static java.lang.Boolean.FALSE;


class MessageDispatcherBankData implements RequestHandler {
    JChannel channel;
    MessageDispatcher disp;
    RspList rsp_list;
    String props; // to be set by application programmer
    LockService lock_service;

    public void start(boolean isMensage, HashMap<String, String> h, RequestOptions opcoes) throws Exception {
        channel = new JChannel(new Protocols().channelProtocols());
        disp = new MessageDispatcher(channel, this);
        channel.connect("MessageDispatcherTestGroup");

        if (isMensage) {
            mensagem(h, opcoes);
        } else {
            eventLoop();
        }
        Util.close(disp, channel);
        //} finally {
        //   lock.unlock();
        //}
    }

    public String mensagem(HashMap h, RequestOptions opcoes) throws Exception {

        ObjectMessage msg = new ObjectMessage(null).setObject(h);
        lock_service = new LockService(channel);
        Lock lock = lock_service.getLock("mylock"); // gets a cluster-wide lock

        rsp_list = disp.castMessage(null,
                msg,
                opcoes);
        lock.lock();
        try {
            if (rsp_list.getResults().contains(1) || rsp_list.getResults().contains(2) || rsp_list.getResults().contains(3)) {
                System.out.println("ERRO");
            }
                System.out.println("Responses:\n" + rsp_list.getResults());
        } finally {
            lock.unlock();
        }
        return "";
    }

    @Override
    public Object handle(org.jgroups.Message msg) throws Exception {

        HashMap msgF = msg.getObject();
        if (msgF.get("tipo").equals("NEW")) {
            System.out.println(msgF.get("tipo"));
            System.out.println(msgF.get("usuario") + " " + msgF.get("senha"));
        } else if (msgF.get("tipo").equals("LOGIN")) {

        } else if (msgF.get("tipo").equals("TRANSFER")) {

        } else if (msgF.get("tipo").equals("TRANSACTIONS")) {

        } else if (msgF.get("tipo").equals("BALACE")) {

        }
        System.out.println("handle(): " + msg.getObject());
        Random gerador = new Random();
        int x = gerador.nextInt(2);
        if (x == 0) {
            return x;
        } else {
            return x;
        }
    }

    private void eventLoop() throws Exception {
        while (channel.getView().getMembers().toArray().length != 0)
            Util.sleep(100); // aguarda o primeiro membro sair do cluster


    }//eventLoop

    public static void main(String[] args) {
        System.out.println();
        try {

            //for (int i = 0; i < 10; i++) {
            Util.sleep(100);
            HashMap<String, String> test = new HashMap();
            test.put("tipo", "NEW");
            test.put("usuario", "user");
            test.put("senha", "123");
            RequestOptions opcoes = new RequestOptions();
            opcoes.setMode(ResponseMode.GET_ALL);
            opcoes.setAnycasting(false);
            System.out.println("Casting message #" + 0);
            opcoes.SYNC();
            new MessageDispatcherBankData().start(true, test, opcoes);
            //  }
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
