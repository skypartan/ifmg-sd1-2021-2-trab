package br.edu.ifmg.sdtrab.util;

import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.Protocol;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ProtocolUtil {

    public static Protocol[] channelProtocols() throws UnknownHostException {
        var masterNodeAddress = System.getenv("MASTER_NODE_HOST");

        return new Protocol[]{
                // UDP Stack
                new UDP().setBindAddr(InetAddress.getByName("127.0.0.1"))/*.setLogDiscardMessages(false)*/,
                new PING(), // Discovery protocol

                // TCP Stack
//                new TCP().setBindAddr(InetAddress.getByName("0.0.0.0"))
//                        .setBindPort(1580),
//                new TCPPING().setPortRange(1)
//                        .setInitialHosts(Collections.singletonList(InetSocketAddress.createUnresolved(masterNodeAddress, 1580))),

                //new BARRIER(), // Needed to transfer state; this will block messages that modify the shared state until a digest has been taken, then unblocks all threads.
                //new MERGE3(), // Merge sub-clusters into one cluster
                new NAKACK2(), // Ensures FIFO and reliability
                new UNICAST3(), // NAKACK for unicast
                //new SEQUENCER(), // Total order for multicast
                new GMS(), // Membership
                new UFC(), // Unicast flow control
                new MFC(), // Multicast flow control
                new FRAG3(),
                //new STATE_TRANSFER(), // Ensures correct state transfers
                //new CENTRAL_LOCK2(),
                new CENTRAL_LOCK(),
                new RATE_LIMITER(),
                //new ASYM_ENCRYPT()

        };
    }
}
