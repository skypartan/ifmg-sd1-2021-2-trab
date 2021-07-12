/*package br.edu.ifmg.sdtrab.communication;

/*
 SAIBA MAIS: http://www.jgroups.org/manual/html/user-building-blocks.html#MessageDispatcher
/**/

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.util.*;

import java.util.*;

/*
 SAIBA MAIS: http://www.jgroups.org/manual/html/user-building-blocks.html#MessageDispatcher
/*

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;

import java.util.*;

public class TiposDeCast extends ReceiverAdapter implements RequestHandler {

    JChannel canalDeComunicacao;
    MessageDispatcher  despachante;

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Código-fonte referente ao JGroups

    private void start() throws Exception {

        //Cria o canal de comunicação com uma configuração XML do JGroups
        //canalDeComunicacao=new JChannel("udp.xml");
        //canalDeComunicacao=new JChannel("sequencer.xml");
        canalDeComunicacao=new JChannel("cast.xml");

        despachante=new MessageDispatcher(canalDeComunicacao, this);

        canalDeComunicacao.setReceiver(canalDeComunicacao.setReceiver(new ReceiverAdapter() {
            public void viewAccepted(View new_view) {
                System.out.println("view: " + new_view);
            }

            public void receive(Message msg) {
                System.out.println("<< " + msg.getObject() + " [" + msg.getSrc() + "]");
            }
        }));

        canalDeComunicacao.connect("TiposDeCast");
        eventLoop();
        canalDeComunicacao.close();

    }

    // extends ReceiverAdapter
    public void receive(Message msg) { //exibe mensagens recebidas
        System.out.println("" + msg.getSrc() + ": " + msg.getObject()); // DEBUG
    }

    // extends ReceiverAdapter
    public void viewAccepted(View new_view) { //exibe alterações na composição do cluster
        System.out.println("\t** nova View do cluster: " + new_view);   // DEBUG
    }


    // implements RequestHandler
    public Object handle(Message msg) throws Exception{ // responde requisições recebidas

        String pergunta = (String) msg.getObject();
        System.out.println("RECEBI uma mensagem: " + pergunta+"\n");  // DEBUG exibe o conteúdo da solicitação

        if(pergunta.contains("concorda"))
            return " SIM "; // resposta padrão desse helloworld à requisição contida na mensagem
        else
            return " NÃO "; // resposta padrão desse helloworld à requisição contida na mensagem
    }

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Código-fonte da minha aplicação de exemplo

    final int TAMANHO_MINIMO_CLUSTER = 4;

    private void eventLoop() {

        Address meuEndereco = canalDeComunicacao.getAddress();

        while( canalDeComunicacao.getView().size() < TAMANHO_MINIMO_CLUSTER )
            Util.sleep(100); // aguarda os membros se juntarem ao cluster

        Vector<Address> cluster = new Vector<Address>(canalDeComunicacao.getView().getMembers()); // CUIDADO: o conteúdo do Vector poderá ficar desatualizado (ex.: se algum membro sair ou entrar na View)
        Address primeiroMembro = cluster.elementAt(0);  //OBS.: 0 a N-1
        Address segundoMembro  = cluster.elementAt(1);  //OBS.: 0 a N-1
        // Address ultimoMembro   = cluster.lastElement();


        // Definiremos um subgrupo do cluster, contendo apenas o 3º e 4º membros (OBS.: 0 a N-1)
        Vector<Address> subgrupo = new Vector<Address>();
        subgrupo.add(cluster.elementAt(2));
        subgrupo.add(cluster.elementAt(3));
        // CUIDADO: o conteúdo do Vector poderá ficar desatualizado (ex.: se algum membro sair ou entrar na View)

        if( meuEndereco.equals(primeiroMembro) ) {  // somente o primeiro membro envia o teste abaixo

            try {
                enviaMulticast( "A maioria dos membros do cluster concorda?" ); //envia multicast para todos do cluster
            }
            catch(Exception e) {
                System.err.println( "ERRO: " + e.toString() );
            }

        } // if primeiro
        else{
            while( canalDeComunicacao.getView().getMembers().contains(primeiroMembro) )
                Util.sleep(100); // aguarda o primeiro membro sair do cluster

            System.out.println("\nBye bye...");
        }

    }//eventLoop


    private RspList enviaMulticast(String conteudo) throws Exception{
        System.out.println("\nENVIEI a pergunta: " + conteudo);

        Address cluster = null; //OBS.: não definir um destinatário significa enviar a TODOS os membros do cluster
        Message msg=new ObjectMessage(null, conteudo);

        RequestOptions opcoes = new RequestOptions();
        opcoes.setMode(ResponseMode.GET_ALL); // ESPERA receber a resposta da MAIORIA dos membros (MAJORITY) // Outras opções: ALL, FIRST, NONE
        opcoes.setAnycasting(false);


        RspList respList = despachante.castMessage(null, msg, opcoes); //envia o MULTICAST
        System.out.println("==> Respostas do cluster ao MULTICAST:\n" +respList+"\n"); //DEBUG: exibe as respostas

        return respList;
    }


    public static void main(String[] args) throws Exception {
        new TiposDeCast().start();
    }

}//class
*/