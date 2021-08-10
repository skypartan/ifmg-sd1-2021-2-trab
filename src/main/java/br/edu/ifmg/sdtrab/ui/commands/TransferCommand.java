package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

public class TransferCommand implements WindowCommand {
    @InjectField
    public ApplicationContext context;

    @Override
    public String getName() {
        return "transferir";
    }

    @Override
    public String getParameters() {
        return "<usuario1> <usuario2> <value>";
    }

    @Override
    public String getDescription() {
        return "Faz tranferencia bancaria";
    }

    @Override
    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
        if (context.getLoggedUser() == null) {
            stdout.println("Primeiro conecte-se à sua conta");
            return;
        }

        stdout.println("Informe os dados: ");

        var scan = new Scanner(stdin);
        stdout.print("\tNome da Pessoa: ");
        var p2 = scan.nextLine();
        stdout.print("\tValor: ");
        var value = scan.nextFloat();

        try {
            var service = context.getNodeController().getClientService();
            var receiver = service.find(p2);
            if (receiver == null) {
                stdout.println("Usuário não encontrado");
                return;
            }

            var sender = service.find(context.getLoggedUser().getName());
            if (sender == null) {
                stdout.println("Falha ao executar transferência");
                return;
            }

            var transference = new Transaction();
            transference.setReceiver(receiver);
            transference.setSender(sender);
            transference.setTime(Timestamp.from(Instant.now()));
            transference.setValue(BigDecimal.valueOf(value));

            if (context.getNodeController().getClientService().transfer(transference)) {
                stdout.println("Transferência concluída");
            }
            else {
                stdout.println("Falha ao processar transferência");
            }
        }
        catch (Exception e) {
            stdout.println("Falha ao executar trasnferência. " + e.getMessage());
        }
    }
}