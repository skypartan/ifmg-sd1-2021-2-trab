package br.edu.ifmg.sdtrab.controller;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.Receiver;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

import java.io.InputStream;
import java.io.OutputStream;

public class ControlController implements RequestHandler, Receiver {

    private JChannel channel;
    private MessageDispatcher dispatcher;

    public ControlController() {

    }

    public void initRole() {

    }

    @Override
    public void receive(Message msg) {

    }

    @Override
    public void viewAccepted(View newView) {

    }

    @Override
    public Object handle(Message msg) throws Exception {
        return null;
    }
}
