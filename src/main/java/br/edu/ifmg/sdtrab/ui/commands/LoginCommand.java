package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;
import br.edu.ifmg.sdtrab.util.PostInject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

public class LoginCommand implements WindowCommand {

    @InjectField
    public ApplicationContext context;

    @PostInject
    public void init() {

    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getParameters() {
        return "<usuario> <senha>";
    }

    @Override
    public String getDescription() {
        return "Libera acesso à sua conta do sistema";
    }

    @Override
    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
        stdout.println("Login executado");
    }
}
