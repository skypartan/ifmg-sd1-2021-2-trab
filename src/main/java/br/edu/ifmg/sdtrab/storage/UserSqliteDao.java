package br.edu.ifmg.sdtrab.storage;

import br.edu.ifmg.sdtrab.entity.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class UserSqliteDao {
    private Connection connection;

    public void insert(User user,String dbName) {
        String sql = "INSERT INTO userBank (name, password, balance) VALUES(?,?,?)";
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setBigDecimal(3, user.getBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<User> selectAll(String dbName) {
        String sql = "SELECT * FROM userBank";
        var list = new ArrayList<User>();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPasswordHash(rs.getString("password"));
                user.setBalance(rs.getBigDecimal("balance"));
                list.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public BigDecimal sumMoney(String dbName) {
        String sql = "SELECT SUM(balance) FROM userBank";
        BigDecimal bg = null;
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            bg = rs.getBigDecimal("SUM(balance)");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return bg;
    }

    public void update(User user, String dbName) {
        String sql = "UPDATE userBank SET name = ?, "
                + "password = ?, "
                + "balance = ?"
                + "WHERE id = ?";
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setBigDecimal(3, user.getBalance());
            pstmt.setInt(4, user.getId());

            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(int id, String dbName) {
        String sql = "DELETE FROM userBank WHERE id = ?";
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setInt(1, id);
            // execute the delete statement
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public User find(int id, String dbName) {
        String sql = "SELECT * FROM userBank WHERE id = ?";
        User user = new User();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPasswordHash(rs.getString("password"));
            user.setBalance(rs.getBigDecimal("balance"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public User find(String name, String dbName) {
        String sql = "SELECT * FROM userBank WHERE name = ?";
        User user = new User();
        connection = new Connector().connect(dbName);
        try (Connection conn = connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs  = pstmt.executeQuery();
            // loop through the result set
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPasswordHash(rs.getString("password"));
            user.setBalance(rs.getBigDecimal("balance"));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

}
