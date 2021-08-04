package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.util.ProtocolUtil;
import org.jgroups.*;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;

import java.util.HashMap;

public class NodeController implements Receiver, RequestHandler {

    private Address address;
    private NodeRole role;

    private JChannel directoryChannel;
    private MessageDispatcher dispatcher;

    private ApplicationContext context;

    public NodeController(ApplicationContext context) {
        this.context = context;

        if (!context.isWorker())
            role = NodeRole.CLIENT_NODE;
    }


    public void init() throws Exception {
        directoryChannel = new JChannel(ProtocolUtil.channelProtocols());
        directoryChannel.setReceiver(this);
        directoryChannel.connect("ebank-directory");
        address = directoryChannel.getAddress();

        dispatcher = new MessageDispatcher(directoryChannel, this);

        if (role == NodeRole.CLIENT_NODE) // Se o nó é cliente ele não deve fazer mais nada
            return;

        // Consultar diretório para verificar qual cargo o nó atual deve atuar
        var options = new RequestOptions();
        options.setMode(ResponseMode.GET_ALL);
        options.setAnycasting(false);
        RequestOptions.SYNC();

        var message = new HashMap<String, String>();
        message.put("task", "controller_query");

        var response = dispatcher.castMessage(null, new ObjectMessage(null, message), options);
        if (response.numReceived() < 2) { // Nó deve se tornar um coordenador
            var received = (HashMap<String, String>) response.getFirst();
            if (received == null || received.get("role").equals("control")) {
                role = NodeRole.STORAGE_CONTROLLER;
                message.put("role", "storage");
            }
            else if (received.get("role").equals("storage")) {
                role = NodeRole.CONTROL_CONTROLLER;
                message.put("role", "control");
            }

            received.put("task", "controller_set");
            dispatcher.castMessage(null, new ObjectMessage(null, received), options);

            dispatcher.close();
            directoryChannel.close();
        }
        else { // Node pode-se conectar à rede de um coordenador

            // Conectar-se a ambos os controles e decidir sob qual operar
            //response.getSuspectedMembers().

            // Iniciar controller de acordo com controlador que se conectou
//            storageController.initRole();
//            controlController.initRole();
        }
    }

    public void changeRole(NodeRole newRole) {

    }


    /**
     *
     * @param newView
     */
    @Override
    public void viewAccepted(View newView) {
        //
    }

    @Override
    public Object handle(Message msg) throws Exception {
        var message = (HashMap<String, String>) msg.getObject();
        if (message.get("task").equals("controller_query")) {
            if (role == NodeRole.CONTROL_CONTROLLER)
                message.put("role", "control");
            if (role == NodeRole.STORAGE_CONTROLLER)
                message.put("role", "storage");

            return new ObjectMessage(null, message);
        }
        if (message.get("task").equals("controller_set")) {
            System.out.println(String.format("Novo controlador: %s -> %s", msg.src(), message.get("task")));
        }

        return null;
    }

    public static enum NodeRole {
        CLIENT_NODE,
        CONTROL_CONTROLLER,
        CONTROL_NODE,
        STORAGE_CONTROLLER,
        STORAGE_NODE
    }

    public Address getAddress() {
        return address;
    }

    public NodeRole getRole() {
        return role;
    }

    public JChannel getDirectoryChannel() {
        return directoryChannel;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public ApplicationContext getContext() {
        return context;
    }
}
