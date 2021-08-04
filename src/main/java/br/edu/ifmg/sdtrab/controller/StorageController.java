package br.edu.ifmg.sdtrab.controller;

import org.jgroups.JChannel;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;

public class StorageController implements Receiver {

    private JChannel channel;
    private MessageDispatcher dispatcher;

    public StorageController() {

    }

    public void initRole() {

    }

    @Override
    public void viewAccepted(View newView) {

    }
}
