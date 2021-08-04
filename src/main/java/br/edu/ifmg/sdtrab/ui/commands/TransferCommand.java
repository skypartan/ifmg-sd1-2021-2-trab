//package br.edu.ifmg.sdtrab.ui.commands;
//
//import br.edu.ifmg.sdtrab.ApplicationContext;
//import br.edu.ifmg.sdtrab.ui.WindowCommand;
//import br.edu.ifmg.sdtrab.util.InjectField;
//
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.util.Scanner;
//
//public class TransferCommand implements WindowCommand {
//    @InjectField
//    public ApplicationContext context;
//
//    @Override
//    public String getName() {
//        return "transferir";
//    }
//
//    @Override
//    public String getParameters() {
//        return "<usuario1> <usuario2> <value>";
//    }
//
//    @Override
//    public String getDescription() {
//        return "Faz tranferencia bancaria";
//    }
//
//    @Override
//    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
//        stdout.println("Informe os dados: ");
//
//        var scan = new Scanner(stdin);
//        stdout.print("\tNome da Pessoa: ");
//        var p2 = scan.nextLine();
//        stdout.print("\tValor: ");
//        var value = scan.nextFloat();
//        var transfer = context.getTransactionController().transfer(context.getLoggedUser(), p2, value);
//        if (transfer != null) {
//            stdout.println(transfer);
//        }
//        else {
//            stdout.println("Falha ao conectar");
//        }
//
//    }
//}
