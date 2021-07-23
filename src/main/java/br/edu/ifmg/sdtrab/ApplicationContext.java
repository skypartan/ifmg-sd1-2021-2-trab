package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.controller.TransactionController;
import br.edu.ifmg.sdtrab.controller.UserController;
import br.edu.ifmg.sdtrab.entity.User;

public class ApplicationContext {

    private User loggedUser;

    private UserController userController;
    private TransactionController transactionController;

    public ApplicationContext() {
        userController = new UserController();
        transactionController = new TransactionController();
        // TODO(lucasgb): Enviar requisição de download do estado atual do sistema
    }

    public void init() throws Exception {
        userController.init();
        transactionController.init();
    }



    //<editor-fold desc="Getters e Setters">

    public UserController getUserController() {
        return userController;
    }

    public void setUserController(UserController userController) {
        this.userController = userController;
    }

    public TransactionController getTransactionController() {
        return transactionController;
    }

    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(User loggedUser) {
        this.loggedUser = loggedUser;
    }

    //</editor-fold>
}
