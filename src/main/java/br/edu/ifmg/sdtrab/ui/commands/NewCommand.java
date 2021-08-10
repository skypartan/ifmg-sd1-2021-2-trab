package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.entity.User;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class NewCommand implements WindowCommand {

    @InjectField
    public ApplicationContext context;

    @Override
    public String getName() {
        return "new";
    }

    @Override
    public String getParameters() {
        return null;
    }

    @Override
    public String getDescription() {
        return "Criar nova conta de usuário";
    }

    @Override
    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
        stdout.println("Iniciada criação de novo usuário");
        stdout.println("Informe os dados: ");

        var scan = new Scanner(stdin);

        stdout.print("\tNome: ");
        var name = scan.nextLine();
        stdout.print("\tSenha: ");
        var password = scan.nextLine();

        try {
            var user = new User();
            user.setName(name);
            user.setPasswordHash(password);
            user = context.getNodeController().getClientService().newUser(user);

            if (user != null) {
                stdout.println("Usuário criado");
            }
            else {
                stdout.println("Falha ao criar usuário");
            }
        }
        catch (Exception e) {
            stdout.println("Falha ao criar usuário. " + e.getMessage());
        }
    }
}
