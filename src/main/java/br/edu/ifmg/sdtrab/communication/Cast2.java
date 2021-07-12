/*package br.edu.ifmg.sdtrab.communication;

import org.jgroups.*;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.Response;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.protocols.pbcast.ViewHandler;

import java.nio.ByteBuffer;
import java.util.List;


public class Cast2  extends ReceiverAdapter implements RequestHandler {
    public static void main(String[] args) throws Exception {
        final JChannel ch = new JChannel("/home/bela/udp.xml");
        ch.setReceiver(new ReceiverAdapter() {
            public void viewAccepted(View new_view) {
                handle(ch, new_view);
            }
        });
        ch.connect("x");

        protected static void handleView (JChannel ch, View new_view){
            if (new_view instanceof MergeView) {
                ViewHandler handler = new ViewHandler(ch, (MergeView) new_view);
                // requires separate thread as we don't want to block JGroups
                handler.resume();
            }
        }

        class ViewHandler extends Thread {
            JChannel ch;
            MergeView view;

            private ViewHandler(JChannel ch, MergeView view) {
                this.ch = ch;
                this.view = view;
            }

            public void run() {
                List<View> subgroups = view.getSubgroups();
                View tmp_view = subgroups.firstElement(); // picks the first
                Address local_addr = ch.getLocalAddress();
                if (!tmp_view.getMembers().contains(local_addr)) {
                    System.out.println("Not member of the new primary partition ("
                            + tmp_view + "), will re-acquire the state");
                    try {
                        ch.getState(null, 30000);
                    } catch (Exception ex) {
                    }
                } else {
                    System.out.println("Not member of the new primary partition ("
                            + tmp_view + "), will do nothing");
                }
            }
        }
    }

    @Override
    public Object handle(Message msg) throws Exception {
        return null;
    }

    @Override
    public void handle(Message request, Response response) throws Exception {

    }

    @Override
    public void receive(Address sender, ByteBuffer buf) {

    }
}
*/