package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;
import br.edu.ifmg.sdtrab.util.PostInject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class BalanceCommand implements WindowCommand {

    @InjectField
    private ApplicationContext context;

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
    public void execute(String[] args, OutputStream stdout) {
        var writer = new PrintWriter(stdout);

        writer.println("Saldo: ?");
    }
}
