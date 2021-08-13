package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.util.NodeRole;
import org.jgroups.Message;
import org.jgroups.View;
import org.jgroups.blocks.RequestHandler;

import java.util.HashMap;

public class NodeController implements RequestHandler {

    private NodeRole role;

    private final DirectoryService directoryService;
    private final ClientService clientService;
    private final ControlController controlService;
    private final StorageController storageService;

    public NodeController(boolean worker) throws Exception {
        if (!worker)
            role = NodeRole.CLIENT_NODE;

        directoryService = new DirectoryService(this);
        clientService = new ClientService(directoryService);
        controlService = new ControlController(this);
        storageService = new StorageController(this);

        directoryService.connect();
    }

    public void decideRole(View view) throws Exception {
        if (role == NodeRole.CLIENT_NODE)
            return;

        if (role != null)
            return;

        System.out.println("Decidindo cargo");

        var control = directoryService.controlController();
        System.out.println("Controlador de controle: " + control);
        var storage = directoryService.storageController();
        System.out.println("Controlador de armazenamento: " + storage);

        if (control == null) {
            role = NodeRole.CONTROL_CONTROLLER;
            controlService.connect();
            storageService.disconnect();
        }
        else if (storage == null) {
            role = NodeRole.STORAGE_CONTROLLER;
            storageService.connect();
            controlService.disconnect();
        }
        else {
            var message = new HashMap<String, Object>();
            message.put("task", "nodes");

            var controlNodesQuery = directoryService.sendMessage(new Message(control, message));
            var storageNodesQuery = directoryService.sendMessage(new Message(storage, message));

            var controlNodeNetwork = (int) controlNodesQuery.getObject();
            var storageNodeNetwork = (int) storageNodesQuery.getObject();

            if (controlNodeNetwork < storageNodeNetwork) {
                role = NodeRole.CONTROL_NODE;
                controlService.connect();
                storageService.disconnect();
            }
            else {
                role = NodeRole.STORAGE_NODE;
                storageService.connect();
                controlService.disconnect();
            }
        }

        System.out.println("Cargo decidido: " + role.name());
    }

    @Override
    public Object handle(Message msg) throws Exception {
        if (msg.getSrc().equals(directoryService.getDirectoryChannel().getAddress()))
            return null;

        var message = (HashMap<String, Object>) msg.getObject();
        System.out.println("Recebido novo comando: " + message);

        if (message.get("task").equals("query")) {
            System.out.println("Informando cargo " + role.name());
            var returns = new Message(msg.src(), role.name());
            returns.setSrc(directoryService.myAddres());
            System.out.println("Mensagem: " + returns);
            return returns;
        }
        if (message.get("task").equals("nodes")) {
            if (role == NodeRole.CONTROL_CONTROLLER) {
                var size = controlService.networkSize();
                var tmp = new Message(msg.src(), size);
                tmp.setSrc(directoryService.myAddres());
                System.out.println("Mensagem: " + tmp);
                return tmp;
            }
            if (role == NodeRole.STORAGE_CONTROLLER) {
                var size = storageService.networkSize();
                var tmp = new Message(msg.src(), size);
                System.out.println("Mensagem: " + tmp);
                return tmp;
            }
        }

        if (message.get("task").equals("control")) {
            if (role == NodeRole.CONTROL_CONTROLLER)
                return controlService.controllerHandle((Message) msg);
        }
        if (message.get("task").equals("storage")) {
            if (role == NodeRole.STORAGE_CONTROLLER)
                return storageService.controllerHandle((Message) msg);
        }

        return null;
    }


    public NodeRole getRole() {
        return role;
    }

    public DirectoryService getDirectoryService() {
        return directoryService;
    }

    public ClientService getClientService() {
        return clientService;
    }

    public ControlController getControlService() {
        return controlService;
    }

    public StorageController getStorageService() {
        return storageService;
    }
}
