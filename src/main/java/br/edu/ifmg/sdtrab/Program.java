package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.ui.MainWindow;

import java.lang.reflect.InvocationTargetException;

public class Program {

    public static void main(String[] args) {
        // UI Thread
        new Thread(() -> {
            try {
                new MainWindow();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }).start();

//        while (true) {
//
//        }
    }
}
