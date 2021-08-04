//package br.edu.ifmg.sdtrab.ui.commands;
//
//import br.edu.ifmg.sdtrab.ApplicationContext;
//import br.edu.ifmg.sdtrab.ui.WindowCommand;
//import br.edu.ifmg.sdtrab.util.InjectField;
//import br.edu.ifmg.sdtrab.util.PostInject;
//
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintStream;
//import java.io.PrintWriter;
//import java.util.Scanner;
//
//public class LoginCommand implements WindowCommand {
//
//    @InjectField
//    public ApplicationContext context;
//
//    @PostInject
//    public void init() {
//
//    }
//
//    @Override
//    public String getName() {
//        return "login";
//    }
//
//    @Override
//    public String getParameters() {
//        return "<usuario> <senha>";
//    }
//
//    @Override
//    public String getDescription() {
//        return "Libera acesso à sua conta do sistema";
//    }
//
//    @Override
//    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
//        stdout.println("Informe os dados: ");
//
//        var scan = new Scanner(stdin);
//
//        stdout.print("\tNome: ");
//        var name = scan.nextLine();
//        stdout.print("\tSenha: ");
//        var password = scan.nextLine();
//
//        var user = context.getUserController().authUser(name, password);
//        if (user != null) {
//            stdout.println("Usuário conectado");
//            context.setLoggedUser(user);
//        }
//        else {
//            stdout.println("Falha ao conectar");
//        }
//    }
//}
