package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.util.ProtocolUtil;
import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class DirectoryService extends ReceiverAdapter {

    private JChannel directoryChannel;
    private MessageDispatcher dispatcher;

    private final NodeController nodeController;

    public DirectoryService(NodeController nodeController) throws Exception {
        this.nodeController = nodeController;
    }

    public void connect() throws Exception {
        System.out.println("Inicializando serviço de diretório");
        directoryChannel = new JChannel(ProtocolUtil.channelProtocols());

        dispatcher = new MessageDispatcher(directoryChannel, this, this, nodeController);

        directoryChannel.setReceiver(this);
        directoryChannel.connect("ebank-directoryChannel");
    }

    public Address controlController() throws Exception {
        System.out.println("Pesquisando controlador da camada de controle");
        var options = new RequestOptions();
        options.setAnycasting(false);
        options.setMode(ResponseMode.GET_ALL);
        //RequestOptions.SYNC();

        var queryMessage = new HashMap<String, Object>();
        queryMessage.put("task", "query");

        System.out.println("Enviando mensagen");
        var msg = new Message(null, queryMessage);
        msg.setSrc(directoryChannel.getAddress());
        var responses = dispatcher.castMessage(null, msg, options);

        var messages = responses.getResults();
        for (Object messageObj : messages) {
            var message = (Message) messageObj;
            if (message.getObject().equals("CONTROL_CONTROLLER")) {
                System.out.println("Encontrado controlador " + message.src());
                return message.src();
            }
        }

        System.out.println("Controlador não encontrado");
        return null;
    }

    public Address storageController() throws Exception {
        var options = new RequestOptions();
        options.setAnycasting(false);
        options.setMode(ResponseMode.GET_ALL);
//        RequestOptions.SYNC();

        var queryMessage = new HashMap<String, Object>();
        queryMessage.put("task", "query");

        var msg = new Message(null, queryMessage);
        msg.setSrc(directoryChannel.getAddress());
        var responses = dispatcher.castMessage(null, msg, options);

        var messages = responses.getResults();
        for (Object messageObj : messages) {
            var message = (Message) messageObj;
            if (message.getObject().equals("STORAGE_CONTROLLER"))
                return message.getSrc();
        }

        return null;
    }

    public Message sendMessage(Message message) throws Exception {
        var options = new RequestOptions();
        options.setAnycasting(false);
        options.setMode(ResponseMode.GET_FIRST);
        RequestOptions.SYNC();
        var response = dispatcher.sendMessage(message, options);

        return (Message) response;
    }

    @Override
    public void viewAccepted(View newView) {
//        if (dispatcher == null)
//            dispatcher = new MessageDispatcher(directoryChannel, nodeController);

        System.out.println("Nós no canal de diretório: " + Arrays.toString(newView.getMembersRaw()));

        new Thread(() -> {
            try {
                nodeController.decideRole(newView);
            }
            catch (Exception e) {
                System.out.println("Não foi possível obter cargo da rede");
                e.printStackTrace();
                System.exit(1);
            }
        }).start();
    }

    public Address myAddres() {
        return directoryChannel.getAddress();
    }

    public JChannel getDirectoryChannel() {
        return directoryChannel;
    }
}
