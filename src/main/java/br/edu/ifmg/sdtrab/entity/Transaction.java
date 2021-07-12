package br.edu.ifmg.sdtrab.entity;

import java.sql.Timestamp;

public class Transaction {

    private User sender;
    private User receiver;
    private float value;

    private Timestamp time;
}
