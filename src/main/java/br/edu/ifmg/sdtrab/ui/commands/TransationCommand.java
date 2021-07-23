package br.edu.ifmg.sdtrab.ui.commands;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.ui.WindowCommand;
import br.edu.ifmg.sdtrab.util.InjectField;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class TransationCommand implements WindowCommand {

    @InjectField
    public ApplicationContext context;

    @Override
    public String getName() {
        return "transacoes";
    }

    @Override
    public String getParameters() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Todas tranferencias bancarias";
    }

    @Override
    public void execute(String[] args, PrintStream stdout, InputStream stdin) {
        ArrayList transation = (ArrayList) context.getTransactionController().transaction(context.getLoggedUser());
        if (transation != null) {
            stdout.println(transation.getClass());
            stdout.println(transation);

            HashMap<String, ArrayList<Transaction>> hs = (HashMap<String, ArrayList<Transaction>>) transation.get(0);
            stdout.println("Recebidos");
            ArrayList<Transaction> rec = hs.get("Recebidos");
            for (int i = 0; i < rec.size(); i++) {
                stdout.println("\tRemetente: " + rec.get(i).getSender().getName());
                stdout.println("\tDestinatário: " + rec.get(i).getReceiver().getName());
                stdout.println("\tValor: " + rec.get(i).getValue());
                stdout.println("\tHorario: " + rec.get(i).getTime());
            }
            stdout.println("\n");
            stdout.println("Enviados");
            ArrayList<Transaction> env = hs.get("Enviados");
            for (int i = 0; i < env.size(); i++) {
                stdout.println("\tRemetente: " + env.get(i).getSender().getName());
                stdout.println("\tDestinatário: " + env.get(i).getReceiver().getName());
                stdout.println("\tValor: " + env.get(i).getValue());
                stdout.println("\tHorario: " + env.get(i).getTime());
            }
        }
        else {
            stdout.println("Não há transferencias");
        }
    }

}
