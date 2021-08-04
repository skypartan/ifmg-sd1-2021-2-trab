package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.storage.TransactionDao;
import br.edu.ifmg.sdtrab.util.ProtocolUtil;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.util.MessageBatch;

import java.util.HashMap;

public class NameService implements RequestHandler, Receiver {
    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private LockService lockService;
    private TransactionDao transactionDao;

    public NameService() {
    }

    public void init() throws Exception {
        channel = new JChannel(new ProtocolUtil().channelProtocols());
        channel.setReceiver(this);
        channel.connect("ebankName");
        dispatcher = new MessageDispatcher(channel, this);
        lockService = new LockService(channel);
        address = channel.getAddress();
    }

    public Object messageView(String who) {
        // TODO(lucasgb): Verificações

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_FIRST);
            options.setAnycasting(false);
            options.SYNC();

            HashMap<String, Object> hs = new HashMap();
            hs.put("tipo", who);

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

    @Override
    public Object handle(Message msg) throws Exception {
        // 0 -> Sucesso
        // 1 -> Usurio não existente
        // 2 -> Dinheiro insuficiente
        // 3 -> Operação não encontrada
        HashMap<String, Object> msgF = msg.getObject();
        if (msgF.get("tipo").equals("VIEW")) {
            //if controller

        }
        return 3;
    }

    @Override
    public void handle(Message request, Response response) throws Exception {

    }

    @Override
    public void receive(Message msg) {

    }

    @Override
    public void receive(MessageBatch batch) {

    }

    @Override
    public void viewAccepted(View new_view) {

    }
}
