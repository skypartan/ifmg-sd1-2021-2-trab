package br.edu.ifmg.sdtrab.storage;

import java.sql.*;

public class Connector {
    public Connection connect(String dbName) {
        // SQLite connection string
        String url = "jdbc:sqlite:./sqlite/db/e-bank-JDBC-"+dbName+".db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
            createNewTable(dbName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void dropDatabase(String dbName) throws SQLException {
        String url = "jdbc:sqlite:./sqlite/db/e-bank-JDBC-"+dbName+".db";

        Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        String sql = "DROP TABLE IF EXISTS 'transactionBank'";

        String sql1 = "DROP TABLE IF EXISTS 'userBank'";
        stmt.execute(sql);
        stmt.execute(sql1);
    }

    public static void createNewTable(String dbName) {
        // SQLite connection string
        String url = "jdbc:sqlite:./sqlite/db/e-bank-JDBC-"+dbName+".db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS userBank (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text,\n"
                + "	password text,\n"
                + "	balance real\n"
                + ");";
        String sql1 = "CREATE TABLE IF NOT EXISTS transactionBank (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	sender_id int,\n"
                + "	receiver_id int,\n"
                + "	ammount real,\n"
                + "	time text\n"
                + ");";


        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
            ///
            stmt.execute(sql1);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
