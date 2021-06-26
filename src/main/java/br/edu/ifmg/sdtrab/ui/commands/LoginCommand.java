package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;
import br.edu.ifmg.sdtrab.util.PostInject;

import java.io.OutputStream;

public class LoginCommand implements WindowCommand {

    @InjectField
    private ApplicationContext context;

    @PostInject
    public void init() {

    }

    @Override
    public String getName() {
        return "login";
    }

    @Override
    public String getParameters() {
        return "<username> <password>";
    }

    @Override
    public String getDescription() {
        return "Libera acesso à sua conta do sistema";
    }

    @Override
    public void execute(String[] args, OutputStream stdout) {

    }
}
