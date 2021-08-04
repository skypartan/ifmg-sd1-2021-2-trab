package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.controller.*;
import br.edu.ifmg.sdtrab.entity.User;

public class ApplicationContext {

    private boolean worker;

    private User loggedUser;

    private NodeController nodeController;
    private ClientController clientController;
    private ControlController controlController;
    private StorageController storageController;

    public ApplicationContext(boolean worker) {
        this.worker = worker;

        nodeController = new NodeController(this);
    }

    public void init() throws Exception {
        nodeController.init();

        if (nodeController.getRole() == NodeController.NodeRole.CONTROL_CONTROLLER)
            controlController.initRole();
        else if (nodeController.getRole() == NodeController.NodeRole.STORAGE_CONTROLLER)
            storageController.initRole();
        else
            clientController.initRole();
    }



    //<editor-fold desc="Getters e Setters">


    public boolean isWorker() {
        return worker;
    }

    public NodeController getNodeController() {
        return nodeController;
    }

    public void setNodeController(NodeController nodeController) {
        this.nodeController = nodeController;
    }

    //</editor-fold>
}
