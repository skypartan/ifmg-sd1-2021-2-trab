package br.edu.ifmg.sdtrab.communication;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.protocols.pbcast.ViewHandler;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;

import java.nio.ByteBuffer;
import java.util.List;


class MessageDispatcherBankCli implements RequestHandler {
    JChannel          channel;
    MessageDispatcher disp;
    RspList           rsp_list;
    String            props; // to be set by application programmer

    public void start() throws Exception {
        channel=new JChannel(props);
        disp=new MessageDispatcher(channel, this);
        channel.connect("MessageDispatcherTestGroup");

        for(int i=0; i < 10; i++) {
            Util.sleep(100);
            System.out.println("Casting message #" + i);
            String pl=("Number #" + i);
            RequestOptions opcoes = new RequestOptions();
            opcoes.setMode(ResponseMode.GET_ALL); // ESPERA receber a resposta da MAIORIA dos membros (MAJORITY) // Outras opções: ALL, FIRST, NONE
            opcoes.setAnycasting(false);
            opcoes.ASYNC();
            rsp_list=disp.castMessage(null,
                    new ObjectMessage(null, pl),
                    opcoes);
            System.out.println("Responses:\n" +rsp_list);
        }
        Util.close(disp,channel);
    }

    public Object handle(Message msg) throws Exception {
        System.out.println("handle(): " +msg.getObject());
        return "Success!";
    }

    public static void main(String[] args) {
        try {
            new MessageDispatcherBankCli().start();
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }
}
