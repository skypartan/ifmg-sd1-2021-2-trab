package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;
import br.edu.ifmg.sdtrab.util.PostInject;

import java.io.PrintStream;
import java.util.HashMap;

public class HelpCommand implements WindowCommand {

    @InjectField
    public ApplicationContext context;
    @InjectField
    public HashMap<String, WindowCommand> commands;

    @PostInject
    public void init() {

    }

    @Override
    public String getName() {
        return "ajuda";
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Exibir lista de comandos suportados pelo sistema";
    }

    @Override
    public void execute(String[] args, PrintStream stdout) {
        stdout.println("Lista de comandos suportados pelo sistema:");
        for (var command : commands.values()) {
            stdout.printf("\t%s -> %s\n", command.getName(), command.getDescription());

            if (command.getParameters() != null)
                stdout.printf("\t\tParâmetros: %s\n", command.getParameters());
        }
    }
}
