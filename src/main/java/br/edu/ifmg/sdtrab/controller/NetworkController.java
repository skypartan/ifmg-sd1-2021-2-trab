package br.edu.ifmg.sdtrab.controller;

import org.jgroups.JChannel;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.locking.LockService;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class NetworkController {

    private JChannel transactionsChannel;
    private JChannel authenticationsChannel;
    private JChannel stateChannel;

    private MessageDispatcher dispatcher;
    private LockService lockService;

    public NetworkController() {

    }

    public void init() throws Exception {
        authenticationsChannel = new JChannel();
        stateChannel = new JChannel();
        transactionsChannel = new JChannel(transactionsProtocols());

        lockService = new LockService(transactionsChannel);
    }

    public void connect() throws Exception {
        transactionsChannel.connect("");

        // ...
    }

    private Protocol[] transactionsProtocols() throws UnknownHostException {
        var masterNodeAddress = System.getenv("MASTER_NODE_HOST");

        return new Protocol[] {
                // UDP Stack
                //new UDP().setValue("bind_addr", InetAddress.getByName("127.0.0.1")),
                //new PING(), // Discovery protocol

                // TCP Stack
                new TCP().setBindAddr(InetAddress.getByName("0.0.0.0"))
                        .setBindPort(1580),
                new TCPPING().setPortRange(1)
                        .setInitialHosts(Collections.singletonList(InetSocketAddress.createUnresolved(masterNodeAddress, 1580))),

                new MERGE3(), // Merge sub-clusters into one cluster
                new NAKACK2(), // Ensures FIFO and reliability
                new UNICAST3(), // NAKACK for unicast
                new GMS(), // Membership
                new UFC(), // Unicast flow control
                new MFC(), // Multicast flow control
                new STATE_TRANSFER(), // Ensures correct state transfers
                new CENTRAL_LOCK2()
        };
    }

    public JChannel getTransactionsChannel() {
        return transactionsChannel;
    }

    public JChannel getAuthenticationsChannel() {
        return authenticationsChannel;
    }

    public JChannel getStateChannel() {
        return stateChannel;
    }
}
