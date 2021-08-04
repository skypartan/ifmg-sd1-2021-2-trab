//package br.edu.ifmg.sdtrab.ui.commands;
//
//import br.edu.ifmg.sdtrab.ApplicationContext;
//import br.edu.ifmg.sdtrab.ui.WindowCommand;
//import br.edu.ifmg.sdtrab.util.InjectField;
//import br.edu.ifmg.sdtrab.util.PostInject;
//
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.math.BigDecimal;
//
//public class MotanteCommad implements WindowCommand {
//
//    @InjectField
//    public ApplicationContext context;
//
//    @Override
//    public String getName() {
//        return "montate";
//    }
//
//    @Override
//    public String getParameters() {
//        return null;
//    }
//
//    @Override
//    public String getDescription() {
//        return "Retorna o motante do banco";
//    }
//
//    @Override
//    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
//        if (context.getLoggedUser() == null) {
//            stdout.println("Primeiro conecte-se à sua conta");
//            return;
//        }
//
//        var montate = new BigDecimal(12);
//        if (montate == null)
//            stdout.println("Falha ao obter montate do banco");
//        else
//            stdout.println("Montate do banco: " + montate);
//    }
//}
//
