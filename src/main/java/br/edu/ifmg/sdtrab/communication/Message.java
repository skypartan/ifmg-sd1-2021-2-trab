package br.edu.ifmg.sdtrab.communication;

import java.io.Serializable;
import java.util.HashMap;

public class Message implements Serializable {

    private String tipo;
    private HashMap entrada;
    private HashMap saida;


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public HashMap getEntrada() {
        return entrada;
    }

    public void setEntrada(HashMap entrada) {
        this.entrada = entrada;
    }

    public HashMap getSaida() {
        return saida;
    }

    public void setSaida(HashMap saida) {
        this.saida = saida;
    }
}
