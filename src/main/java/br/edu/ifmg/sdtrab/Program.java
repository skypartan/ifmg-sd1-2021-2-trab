package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.ui.MainWindow;

public class Program {

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                new MainWindow();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }).start();
    }
}
