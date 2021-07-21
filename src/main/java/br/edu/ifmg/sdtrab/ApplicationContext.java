package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.controller.TransactionController;
import br.edu.ifmg.sdtrab.controller.UserController;

public class ApplicationContext {

    private UserController userController;
    private TransactionController transactionController;

    public ApplicationContext() {
        userController = new UserController();

        // TODO(lucasgb): Enviar requisição de download do estado atual do sistema
    }

    public void init() throws Exception {
        userController.init();
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

    //</editor-fold>
}
