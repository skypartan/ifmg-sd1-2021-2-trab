package br.edu.ifmg.sdtrab.communication;

import br.edu.ifmg.sdtrab.util.ProtocolUtil;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.RATE_LIMITER;
import org.jgroups.tests.Probe;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


class MessageDispatcherBankData implements RequestHandler, Receiver {
    JChannel channel;
    MessageDispatcher disp;
    RspList rsp_list;
    String props; // to be set by application programmer
    LockService lock_service;

    public void start(boolean isMensage, HashMap<String, String> h, RequestOptions opcoes) throws Exception {
        channel = new JChannel(new ProtocolUtil().channelProtocols());
        channel.setReceiver(this);
        disp = new MessageDispatcher(channel, this);
        channel.connect("MessageDispatcherTestGroup");
        RATE_LIMITER rate = new RATE_LIMITER();
        rate.setMaxBytes(50);
        rate.setTimePeriod(1000);
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


    @Override
    public void getState(OutputStream output) throws Exception {
        Object state = new Object();
        synchronized(state) {
            Util.objectToStream(state, new DataOutputStream(output));
        }
    }

    @Override
    public void setState(InputStream input) throws Exception {
        List<String> list;
        list=(List<String>)Util.objectFromStream(new DataInputStream(input));
        List state = new ArrayList();
        synchronized(state) {
            state.clear();
            state.addAll(list);
        }
        System.out.println(list.size() + " messages in chat history):");
        for(String str: list)
            System.out.println(str);
    }

    // Mensagem recebida
    @Override
    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    // Mudança na estrutura de clientes conectados
    public void viewAccepted(View new_view) {
        System.out.println("** view: " + new_view);
    }

    public String mensagem(HashMap h, RequestOptions opcoes) throws Exception {

        //ObjectMessage msg = new ObjectMessage(null).setObject(h);
        //lock_service = new LockService(channel);
        //Lock lock = lock_service.getLock("mylock"); // gets a cluster-wide lock
        System.out.println(channel.getAddressAsString());
        rsp_list = disp.castMessage(null,
                new ObjectMessage(null, h),
                opcoes);
        //lock.lock();
        // try {
        if (rsp_list.getResults().contains(1) || rsp_list.getResults().contains(2) || rsp_list.getResults().contains(3)) {
            System.out.println("ERRO");
        }
        System.out.println("Responses:\n" + rsp_list);

       // while(rsp_list.iterator().hasNext()) {
            Object element = rsp_list.toString();
            System.out.print(rsp_list.toString().split(":")[0].replace("[", "") + " ");
            System.out.print(rsp_list.get(rsp_list.toString().split(":")[0].replace("[", ""))+ " ");

        //}
        //  } finally {
        //     lock.unlock();
        //}
        return "";
    }

    @Override
    public Object handle(org.jgroups.Message msg) throws Exception {
        HashMap msgF = msg.getObject();
        System.out.println(this.getClass());
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
