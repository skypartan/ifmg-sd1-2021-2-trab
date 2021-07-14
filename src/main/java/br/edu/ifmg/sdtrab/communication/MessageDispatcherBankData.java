package br.edu.ifmg.sdtrab.communication;

import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

import java.math.BigDecimal;
import java.util.HashMap;


class MessageDispatcherBankData implements RequestHandler {
    JChannel          channel;
    MessageDispatcher disp;
    RspList           rsp_list;
    String            props; // to be set by application programmer
    //LockService lock_service;

    public void start() throws Exception {
        //ch=new JChannel("/home/bela/locking.xml");
        channel=new JChannel(props);
        //LockService lock_service=new LockService(channel);
        disp=new MessageDispatcher(channel, this);
        channel.connect("MessageDispatcherTestGroup");
        //Lock lock = lock_service.getLock("mylock"); // gets a cluster-wide lock
        //lock.lock();
        //try {
            for (int i = 0; i < 10; i++) {
                Util.sleep(100);
                HashMap<String, String> test = new HashMap();
                test.put( "usuario", "user");
                test.put("senha", "123");
                Message u = new Message();
                u.setTipo("NEW");
                u.setEntrada(test);
                RequestOptions opcoes = new RequestOptions();
                opcoes.setMode(ResponseMode.GET_ALL);
                opcoes.setAnycasting(false);
                System.out.println("Casting message #" + i);
                opcoes.ASYNC();
                ObjectMessage msg = new ObjectMessage(null).setObject(u);
                rsp_list = disp.castMessage(null,
                        msg,
                        opcoes);
                System.out.println("Responses:\n" + rsp_list);
            }
            Util.close(disp, channel);
        //} finally {
         //   lock.unlock();
        //}
    }

    @Override
    public Object handle(org.jgroups.Message msg) throws Exception {
        Message msgF = msg.getObject();
        if (msgF.getTipo().equals("NEW")){
            System.out.println(msgF.getTipo());
            HashMap<String, String> e = msgF.getEntrada();
            System.out.println(e.get("usuario")+" "+e.get("senha"));
        } else if (msgF.getTipo().equals("LOGIN")){

        } else if (msgF.getTipo().equals("TRANSFER")) {

        } else if (msgF.getTipo().equals("TRANSACTIONS")) {

        } else if (msgF.getTipo().equals("BALACE")) {

        }
        System.out.println("handle(): " +msg.getObject());

        return "Success!";
    }

    public static void main(String[] args) {
        try {
            new MessageDispatcherBankData().start();
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }


}
