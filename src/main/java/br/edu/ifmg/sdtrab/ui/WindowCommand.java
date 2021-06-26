package br.edu.ifmg.sdtrab.ui;

import java.io.OutputStream;

public interface WindowCommand {

    String getName();
    String getParameters();
    String getDescription();

    void execute(String[] args, OutputStream stdout);
}
