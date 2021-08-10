package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.util.NodeRole;
import org.jgroups.Message;
import org.jgroups.ObjectMessage;
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

    public void decideRole() throws Exception {
        if (role == NodeRole.CLIENT_NODE)
            return;

        System.out.println("Decidindo cargo");

        var control = directoryService.controlController();
        var storage = directoryService.storageController();

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

            var controlNodesQuery = directoryService.sendMessage(new ObjectMessage(control, message));
            var storageNodesQuery = directoryService.sendMessage(new ObjectMessage(storage, message));

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
        if (msg.getSrc() == directoryService.myAddres())
            return null;

        var message = (HashMap<String, Object>) msg.getObject();
        System.out.println("Recebido novo comando: " + message);

        if (message.get("task").equals("query"))
            return new ObjectMessage(msg.src(), role.name()).setSrc(directoryService.myAddres());
        if (message.get("task").equals("nodes")) {
            if (role == NodeRole.CONTROL_CONTROLLER)
                return new ObjectMessage(msg.src(), controlService.networkSize()).setSrc(directoryService.myAddres());
            if (role == NodeRole.STORAGE_CONTROLLER)
                return new ObjectMessage(msg.src(), storageService.networkSize()).setSrc(directoryService.myAddres());
        }

        if (message.get("task").equals("control")) {
            if (role == NodeRole.CONTROL_CONTROLLER)
                return controlService.controllerHandle((ObjectMessage) msg);
        }
        if (message.get("task").equals("storage")) {
            if (role == NodeRole.STORAGE_CONTROLLER)
                return storageService.controllerHandle((ObjectMessage) msg);
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
