package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;

import java.io.InputStream;
import java.io.PrintStream;

public class MotanteCommad implements WindowCommand {

    @InjectField
    public ApplicationContext context;

    @Override
    public String getName() {
        return "montate";
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Retorna o motante do banco";
    }

    @Override
    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
        if (context.getLoggedUser() == null) {
            stdout.println("Primeiro conecte-se à sua conta");
            return;
        }

        try {
            var montante = context.getNodeController().getClientService().totalMoney();
            if (montante == null)
                stdout.println("Falha ao obter montate no banco");
            else
                stdout.println("Montate do banco: " + montante);
        }
        catch (Exception e) {
            System.out.println("Falha ao consutar saldo do usuário. " + e.getMessage());
        }
    }
}

