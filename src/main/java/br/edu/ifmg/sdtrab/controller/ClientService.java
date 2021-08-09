package br.edu.ifmg.sdtrab.controller;

import org.jgroups.ObjectMessage;
import org.jgroups.util.RspList;

import java.util.HashMap;

public class ClientService {

    private DirectoryService directoryService;

    public ClientService(DirectoryService directoryService) {

    }

    public void doSomething() throws Exception {
        var controller = directoryService.controlController();

        // ...
    }

}
