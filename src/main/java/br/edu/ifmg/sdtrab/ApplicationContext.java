package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.controller.NetworkController;
import br.edu.ifmg.sdtrab.controller.TransactionController;
import br.edu.ifmg.sdtrab.controller.UserController;

public class ApplicationContext {

    private NetworkController networkController;
    private UserController userController;
    private TransactionController transactionController;

    public ApplicationContext() {
        networkController = new NetworkController();
    }

    public NetworkController getNetworkController() {
        return networkController;
    }

    public void setNetworkController(NetworkController networkController) {
        this.networkController = networkController;
    }
}
