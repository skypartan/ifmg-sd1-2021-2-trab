package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;

import java.io.PrintStream;

public class NewCommand implements WindowCommand {

    @InjectField
    public ApplicationContext context;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void execute(String[] args, PrintStream stdout) {

    }
}
