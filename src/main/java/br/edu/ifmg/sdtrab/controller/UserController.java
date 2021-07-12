package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.User;
import org.jgroups.JChannel;

public class UserController {

    private final NetworkController networkController;
    private JChannel channel;

    public UserController(NetworkController networkController) {
        this.networkController = networkController;

        channel = networkController.getAuthenticationsChannel();
    }

    public User newUser(String name, String password) {
        return null;
    }
}
