package br.edu.ifmg.sdtrab.ui;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.commands.BalanceCommand;
import br.edu.ifmg.sdtrab.ui.commands.HelpCommand;
import br.edu.ifmg.sdtrab.ui.commands.LoginCommand;
import br.edu.ifmg.sdtrab.util.CommandBuilder;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Scanner;

public class MainWindow {

    private ApplicationContext context;
    private HashMap<String, WindowCommand> commands = new HashMap<>();

    private boolean running = true;
    private final Scanner scanner = new Scanner(System.in);

    public MainWindow() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        registerCommands();

        windowLoop();
    }

    public void windowLoop() {
        System.out.println(motd());

        while (running) {
            System.out.print("> ");
            System.out.flush();

            var input = scanner.nextLine();
            var args = input.split("\\s+");

            if (commands.containsKey(args[0])) {
                var command = commands.get(args[0]);
                command.execute(args, System.out);
                System.out.flush();
            }

            if (input.equals("exit")) {
                System.out.println("Saindo...");
                running = false;
            }
        }
    }

    public String motd() {
        return "Internet Banking v1.0\n";
    }

    public void registerCommands() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            InstantiationException {

        new CommandBuilder(context)
                .type(HelpCommand.class)
                .registry(commands)
                .build();

        new CommandBuilder(context)
                .type(LoginCommand.class)
                .registry(commands)
                .build();

        new CommandBuilder(context)
                .type(BalanceCommand.class)
                .registry(commands)
                .build();
    }
}
