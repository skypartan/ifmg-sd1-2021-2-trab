package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.ui.MainWindow;

public class Program {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Informe se é client ou server");
            System.exit(1);
        }

        boolean worker = true;
        if (args[1].equals("client"))
            worker = false;

        var context = new ApplicationContext(worker);
        try {
            context.init();

            if (worker) {

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
