package br.edu.ifmg.sdtrab.storage;

import br.edu.ifmg.sdtrab.entity.Transaction;
import br.edu.ifmg.sdtrab.entity.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    private Connection connection;
    private UserDao userDao;

    public TransactionDao() throws IOException, SQLException {
        //connection = new ConnectionBuilder().build();
        userDao = new UserDao();
    }

    public boolean save(Transaction transaction) throws SQLException {
        var sql = "insert into transaction (sender_id, receiver_id, ammount, time) values (?, ?, ?, ?);";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, transaction.getSender().getId());
        statement.setInt(2, transaction.getReceiver().getId());
        statement.setBigDecimal(3, transaction.getValue());
        statement.setTimestamp(4, transaction.getTime());
        return statement.execute();
    }

    public boolean delete(Transaction transaction) throws SQLException {
        var sql = "delete from transaction where id = ?;";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, transaction.getId());
        return statement.executeUpdate() > 0;
    }

    public Transaction find(int id) throws SQLException {
        var sql = "select * from transaction where id = ?;";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);

        var result = statement.executeQuery();
        if (!result.first())
            return null;

        var transaction = new Transaction();
        transaction.setId(result.getInt(1));
        transaction.setSender(userDao.find(result.getInt(2)));
        transaction.setReceiver(userDao.find(result.getInt(3)));
        transaction.setValue(result.getBigDecimal(4));
        transaction.setTime(result.getTimestamp(5));
        return transaction;
    }

    public ArrayList<Transaction> findbyReceiverId(int id) throws SQLException {
        var sql = "select * from transaction where receiver_id = ?;";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        var list = new ArrayList<Transaction>();
        var transaction = new Transaction();
        var result = statement.executeQuery();

        while (result.next()) {
            transaction.setId(result.getInt(1));
            transaction.setSender(userDao.find(result.getInt(2)));
            transaction.setReceiver(userDao.find(result.getInt(3)));
            transaction.setValue(result.getBigDecimal(4));
            transaction.setTime(result.getTimestamp(5));
            list.add(transaction);
        }
        return list;
    }

    public ArrayList<Transaction> findbySenderId(int id) throws SQLException {
        var sql = "select * from transaction where sender_id = ?;";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        var list = new ArrayList<Transaction>();
        var transaction = new Transaction();
        var result = statement.executeQuery();


        while (result.next()) {
            transaction.setId(result.getInt(1));
            transaction.setSender(userDao.find(result.getInt(2)));
            transaction.setReceiver(userDao.find(result.getInt(3)));
            transaction.setValue(result.getBigDecimal(4));
            transaction.setTime(result.getTimestamp(5));
            list.add(transaction);
        }
        return list;
    }

    public List<Transaction> search(String query) throws SQLException {
        var sql = "select * from transaction ?;";
        var statement = connection.prepareStatement(sql);
        statement.setString(1, query);

        var result = statement.executeQuery();
        var list = new ArrayList<Transaction>();

        while (result.next()) {
            var transaction = new Transaction();
            transaction.setId(result.getInt(1));
            transaction.setSender(userDao.find(result.getInt(2)));
            transaction.setReceiver(userDao.find(result.getInt(3)));
            transaction.setValue(result.getBigDecimal(4));
            transaction.setTime(result.getTimestamp(5));
            list.add(transaction);
        }

        return list;
    }
}
