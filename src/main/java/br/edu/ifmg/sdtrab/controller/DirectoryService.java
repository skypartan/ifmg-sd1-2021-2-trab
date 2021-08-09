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

public class DirectoryService implements Receiver {

    private JChannel directoryChannel;
    private MessageDispatcher dispatcher;

    private final NodeController nodeController;

    public DirectoryService(NodeController nodeController) throws Exception {
        this.nodeController = nodeController;
    }

    public void connect() throws Exception {
        System.out.println("Inicializando serviço de diretório");
        directoryChannel = new JChannel(ProtocolUtil.channelProtocols());
        directoryChannel.setReceiver(this);
        directoryChannel.connect("ebank-directoryChannel");
    }

    public Address controlController() throws Exception {
        var options = new RequestOptions();
        options.setAnycasting(false);
        options.setMode(ResponseMode.GET_ALL);
        RequestOptions.SYNC();
        var responses = dispatcher.castMessage(null, new ObjectMessage(null, "QUERY"), options);

        var messages = responses.getResults();
        for (Object messageObj : messages) {
            var message = (ObjectMessage) messageObj;
            if (message.getObject().equals("CONTROL_CONTROLLER"))
                return message.src();
        }

        return null;
    }

    public Address storageController() throws Exception {
        var options = new RequestOptions();
        options.setAnycasting(false);
        options.setMode(ResponseMode.GET_ALL);
        RequestOptions.SYNC();
        var responses = dispatcher.castMessage(null, new ObjectMessage(null, "QUERY"), options);

        var messages = responses.getResults();
        for (Object messageObj : messages) {
            var message = (ObjectMessage) messageObj;
            if (message.getObject().equals("STORAGE_CONTROLLER"))
                return message.getSrc();
        }

        return null;
    }

    public ObjectMessage sendMessage(ObjectMessage message) throws Exception {
        var options = new RequestOptions();
        options.setAnycasting(false);
        options.setMode(ResponseMode.GET_FIRST);
        RequestOptions.SYNC();
        var response = dispatcher.castMessage(Collections.singletonList(message.getDest()), message, options);

        return (ObjectMessage) response.getFirst();
    }

    @Override
    public void viewAccepted(View newView) {
        if (dispatcher == null)
            dispatcher = new MessageDispatcher(directoryChannel, nodeController);

        System.out.println("Visão da rede: " + Arrays.toString(newView.getMembersRaw()));

        try {
            nodeController.decideRole();
        }
        catch (Exception e) {
            System.out.println("Não foi possível obter cargo da rede");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Address myAddres() {
        return directoryChannel.getAddress();
    }
}
