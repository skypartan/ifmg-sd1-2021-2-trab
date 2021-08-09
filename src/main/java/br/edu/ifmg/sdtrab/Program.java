package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.ui.MainWindow;

public class Program {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Informe se é client ou server");
            System.exit(1);
        }

        boolean worker = !args[0].equals("client");

        try {
            var context = new ApplicationContext(worker);

            if (worker) {
                while (true) { }
            }
            else {
                new MainWindow(context);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
