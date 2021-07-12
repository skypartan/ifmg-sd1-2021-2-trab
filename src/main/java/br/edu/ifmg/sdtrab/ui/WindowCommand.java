package br.edu.ifmg.sdtrab.ui;

import java.io.OutputStream;
import java.io.PrintStream;

public interface WindowCommand {

    String getName();
    String getParameters();
    String getDescription();

    void execute(String[] args, PrintStream stdout);
}
