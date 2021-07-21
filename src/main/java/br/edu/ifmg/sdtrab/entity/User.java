package br.edu.ifmg.sdtrab.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class User implements Serializable {

    private int id;
    private String name;
    private String passwordHash;

    private BigDecimal balance;

    public User() {
        balance = BigDecimal.valueOf(10000);
    }

    public User(String name, String password) {
        this.name = name;
        this.passwordHash = password;
        balance = BigDecimal.valueOf(10000);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
