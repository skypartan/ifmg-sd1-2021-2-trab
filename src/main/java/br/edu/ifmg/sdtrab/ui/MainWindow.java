package br.edu.ifmg.sdtrab.ui;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.commands.BalanceCommand;
import br.edu.ifmg.sdtrab.ui.commands.LoginCommand;
import br.edu.ifmg.sdtrab.util.CommandBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Scanner;

public class MainWindow {

    private ApplicationContext context;
    private HashMap<String, WindowCommand> commands;

    private boolean running = true;
    private final Scanner scanner = new Scanner(System.in);

    public MainWindow() {
        windowLoop();
    }

    public void windowLoop() {

        System.out.println(motd());

        var input = scanner.nextLine();
        while (running) {

            // ...

            if (input.equals("exit")) {
                System.out.println("Saindo...");
                System.exit(0);
            }
        }
    }

    public String motd() {
        return "";
    }

    public void registerCommands() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            InstantiationException {

        new CommandBuilder(context)
                .type(LoginCommand.class)
                .registry(commands)
                .build();

        new CommandBuilder(context)
                .type(BalanceCommand.class)
                .registry(commands)
                .build();
    }

    public void invokeCommand(WindowCommand command) {

    }
}
