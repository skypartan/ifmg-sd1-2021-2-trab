package br.edu.ifmg.sdtrab.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Transaction implements Serializable {

    private int id;
    private User sender;
    private User receiver;
    private BigDecimal value;

    private Timestamp time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
