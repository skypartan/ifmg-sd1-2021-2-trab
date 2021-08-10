package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.controller.NodeController;
import br.edu.ifmg.sdtrab.entity.User;

public class ApplicationContext {

    private User loggedUser;
    private NodeController nodeController;

    public ApplicationContext(boolean worker) throws Exception {
        nodeController = new NodeController(worker);
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    public NodeController getNodeController() {
        return nodeController;
    }
}
