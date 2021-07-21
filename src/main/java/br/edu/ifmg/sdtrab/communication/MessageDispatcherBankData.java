package br.edu.ifmg.sdtrab.communication;

import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Scanner;

import static java.lang.Boolean.FALSE;


class MessageDispatcherBankData implements RequestHandler {
    JChannel channel;
    MessageDispatcher disp;
    RspList rsp_list;
    String props; // to be set by application programmer
    //LockService lock_service;

    public void start(boolean isMensage, HashMap<String, String> h, RequestOptions opcoes) throws Exception {
        //ch=new JChannel("/home/bela/locking.xml");
        channel = new JChannel(props);
        //LockService lock_service=new LockService(channel);
        disp = new MessageDispatcher(channel, this);
        channel.connect("MessageDispatcherTestGroup");
        //Lock lock = lock_service.getLock("mylock"); // gets a cluster-wide lock
        //lock.lock();
        //try {
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
        rsp_list = disp.castMessage(null,
                msg,
                opcoes);
        System.out.println("Responses:\n" + rsp_list.getResults());
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

        return "Success!";
    }

    private void eventLoop() throws Exception {
        while (channel.getView().getMembers().toArray().length != 0)
            Util.sleep(100); // aguarda o primeiro membro sair do cluster


    }//eventLoop

    public static void main(String[] args) {
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
