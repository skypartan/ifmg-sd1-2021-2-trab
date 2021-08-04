package br.edu.ifmg.sdtrab.storage;

import br.edu.ifmg.sdtrab.entity.Transaction;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TransactionSqliteDao {
    private Connection connection;
    private UserSqliteDao userSqliteDao;

    public TransactionSqliteDao() {
        userSqliteDao = new UserSqliteDao();
    }

    public void save(Transaction transaction, String dbName) {
        String sql = "INSERT INTO transactionBank (sender_id, receiver_id, ammount, time) VALUES(?,?,?,?)";
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transaction.getSender().getId());
            pstmt.setInt(2, transaction.getReceiver().getId());
            pstmt.setBigDecimal(3, transaction.getValue());
            pstmt.setString(4, transaction.getTime().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<Transaction> selectAll(String dbName) {
        String sql = "SELECT * FROM transactionBank";
        ArrayList<Transaction> list = new ArrayList<>();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setValue(rs.getBigDecimal("ammount"));
                transaction.setSender(userSqliteDao.find(rs.getInt("sender_id"), dbName));
                transaction.setReceiver(userSqliteDao.find(rs.getInt("receiver_id"), dbName));
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Date parsedDate = (Date) dateFormat.parse(rs.getString("time"));
                    Timestamp timestamp = new Timestamp(parsedDate.getTime());
                    transaction.setTime(timestamp);
                } catch(Exception e) {
                    System.out.println("Error: " + e);
                }
                list.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public void update(Transaction transaction, String dbName) {
        String sql = "UPDATE transactionBank SET sender_id = ?,"
                + " receiver_id = ?,"
                + " ammount = ?,"
                + " time = ?"
                + "WHERE id = ?";
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, transaction.getSender().getId());
            pstmt.setInt(2, transaction.getReceiver().getId());
            pstmt.setBigDecimal(3, transaction.getValue());
            pstmt.setString(4, transaction.getTime().toString());
            pstmt.setInt(5, transaction.getId());

            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(Transaction transaction,String dbName) {
        String sql = "DELETE FROM transactionBank WHERE id = ?";
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, transaction.getId());
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Transaction find(int id, String dbName) {
        String sql = "SELECT * FROM transactionBank WHERE id = ?";
        Transaction transaction = new Transaction();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            transaction.setId(rs.getInt("id"));
            transaction.setValue(rs.getBigDecimal("ammount"));
            transaction.setSender(userSqliteDao.find(rs.getInt("sender_id"), dbName));
            transaction.setReceiver(userSqliteDao.find(rs.getInt("receiver_id"), dbName));
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                Date parsedDate = (Date) dateFormat.parse(rs.getString("time"));
                Timestamp timestamp = new Timestamp(parsedDate.getTime());
                transaction.setTime(timestamp);
            } catch(Exception e) {
                System.out.println("Error: " + e);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transaction;
    }

    public ArrayList<Transaction> findbyReceiverId(int id,String dbName) {
        String sql = "SELECT * FROM transactionBank WHERE receiver_id = ?";
        ArrayList<Transaction> list = new ArrayList<>();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setValue(rs.getBigDecimal("ammount"));
                transaction.setSender(userSqliteDao.find(rs.getInt("sender_id"), dbName));
                transaction.setReceiver(userSqliteDao.find(rs.getInt("receiver_id"), dbName));
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS", Locale.ENGLISH);
                    Date date = formatter.parse(rs.getString("time"));
                    Timestamp timestamp = new Timestamp(date.getTime());
                    transaction.setTime(timestamp);
                }catch (Exception e){
                    System.out.println("Erro: "+ e);
                }

                list.add(transaction);
           }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public ArrayList<Transaction> findbySenderId(int id,String dbName) {
        String sql = "SELECT * FROM transactionBank WHERE sender_id = ?";
        ArrayList<Transaction> list = new ArrayList<>();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            ResultSet rs  = pstmt.executeQuery();

            // loop through the result set
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getInt("id"));
                transaction.setValue(rs.getBigDecimal("ammount"));
                transaction.setSender(userSqliteDao.find(rs.getInt("sender_id"), dbName));
                transaction.setReceiver(userSqliteDao.find(rs.getInt("receiver_id"), dbName));
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                    Date parsedDate = (Date) dateFormat.parse(rs.getString("time"));
                    Timestamp timestamp = new Timestamp(parsedDate.getTime());
                    transaction.setTime(timestamp);
                } catch(Exception e) {
                    System.out.println("Error: " + e);
                }
                list.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

}
