package br.edu.ifmg.sdtrab.controller;

import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.storage.UserDao;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;
import org.jgroups.util.MessageBatch;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;

public class UserController implements RequestHandler, Receiver {

    private JChannel channel;
    private Address address;
    private MessageDispatcher dispatcher;
    private UserDao userDao;

    public UserController() {

    }

    public void init() throws Exception {
        channel = new JChannel(channelProtocols());
        channel.setReceiver(this);
        channel.connect("ebank");
        dispatcher = new MessageDispatcher(channel, this);

        address = channel.getAddress();

        userDao = new UserDao();
    }

    public void close() {

    }

    public User newUser(String name, String password) {
        System.out.println("newUser");
        // TODO(lucasgb): Verificações

        var user = new User(name, password);

        try {
            var options = new RequestOptions();
            options.setMode(ResponseMode.GET_ALL);
            options.setAnycasting(false);

            var list = dispatcher.castMessage(null, new ObjectMessage(null, "teste"), options);
            if (list == null)
                return null;

            // usuario aceito
            // cadastrar

            userDao.save(user);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean authUser(String name, String password) {
        return false;
    }

    public void saveUser(User user) {

    }


    // Mensagem recebida
    @Override
    public void receive(Message msg) {
        System.out.println(msg.getSrc() + ": " + msg.getObject());
    }

    // Mudança na estrutura de clientes conectados
    public void viewAccepted(View new_view) {
        //System.out.println("** view: " + new_view);
    }

    // Processar requisição síncrona
    @Override
    public Object handle(Message msg) throws Exception {
        System.out.println("handle sync");
        return null;
    }

    // Processar requisição assíncrona
    @Override
    public void handle(Message request, Response response) throws Exception {
        System.out.println("handle async");
    }

    private Protocol[] channelProtocols() throws UnknownHostException {
        var masterNodeAddress = System.getenv("MASTER_NODE_HOST");

        return new Protocol[] {
                // UDP Stack
                new UDP().setBindAddr(InetAddress.getByName("127.0.0.1")),
                new PING(), // Discovery protocol

                // TCP Stack
//                new TCP().setBindAddr(InetAddress.getByName("0.0.0.0"))
//                        .setBindPort(1580),
//                new TCPPING().setPortRange(1)
//                        .setInitialHosts(Collections.singletonList(InetSocketAddress.createUnresolved(masterNodeAddress, 1580))),

                new BARRIER(), // Needed to transfer state; this will block messages that modify the shared state until a digest has been taken, then unblocks all threads.
                new MERGE3(), // Merge sub-clusters into one cluster
                new NAKACK2(), // Ensures FIFO and reliability
                new UNICAST3(), // NAKACK for unicast
                new SEQUENCER(), // Total order for multicast
                new GMS(), // Membership
                new UFC(), // Unicast flow control
                new MFC(), // Multicast flow control
                new STATE_TRANSFER(), // Ensures correct state transfers
                //new CENTRAL_LOCK2()
        };
    }
}
