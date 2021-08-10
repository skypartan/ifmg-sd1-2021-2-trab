package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;
import br.edu.ifmg.sdtrab.util.PostInject;

import java.io.*;

public class BalanceCommand implements WindowCommand {

    @InjectField
    public ApplicationContext context;

    @PostInject
    public void init() {

    }

    @Override
    public String getName() {
        return "saldo";
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Retorna o saldo de sua conta";
    }

    @Override
    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
        if (context.getLoggedUser() == null) {
            stdout.println("Primeiro conecte-se à sua conta");
            return;
        }

        try {
            var user = context.getNodeController().getClientService().find(context.getLoggedUser().getName());
            if (user == null)
                stdout.println("Falha ao obter saldo");
            else
                stdout.println("Saldo: " + user.getBalance());
        }
        catch (Exception e) {
            stdout.println("Falha ao obter saldo. " + e.getMessage());
        }
    }
}
