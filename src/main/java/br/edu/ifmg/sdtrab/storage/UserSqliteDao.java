package br.edu.ifmg.sdtrab.storage;

import br.edu.ifmg.sdtrab.entity.User;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

public class UserSqliteDao {
    private Connection connection;

    public void save(User user, String dbName) throws SQLException {
        String sql = "INSERT INTO userBank (name, password, balance) VALUES(?,?,?)";
        connection = new Connector().connect(dbName);

        Connection conn = connection;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getPasswordHash());
        pstmt.setBigDecimal(3, user.getBalance());
        pstmt.executeUpdate();

    }

    public ArrayList<User> selectAll(String dbName) throws SQLException {
        String sql = "SELECT * FROM userBank";
        var list = new ArrayList<User>();
        connection = new Connector().connect(dbName);

        Connection conn = connection;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);


        // loop through the result set
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setPasswordHash(rs.getString("password"));
            user.setBalance(rs.getBigDecimal("balance"));
            list.add(user);
        }

        return list;
    }

    public BigDecimal sumMoney(String dbName) throws SQLException {
        String sql = "SELECT SUM(balance) FROM userBank";

        BigDecimal bg = null;
        connection = new Connector().connect(dbName);

        Connection conn = connection;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        // loop through the result set
        bg = rs.getBigDecimal("SUM(balance)");
        return bg;
    }

    public void update(User user, String dbName) throws SQLException {

        String sql = "UPDATE userBank SET name = ?, "
                + "password = ?, "
                + "balance = ?"
                + "WHERE id = ?";
        connection = new Connector().connect(dbName);
        Connection conn = connection;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // set the corresponding param
        pstmt.setString(1, user.getName());
        pstmt.setString(2, user.getPasswordHash());
        pstmt.setBigDecimal(3, user.getBalance());
        pstmt.setInt(4, user.getId());

        // update
        pstmt.executeUpdate();

    }

    public void delete(int id, String dbName) throws SQLException {
        String sql = "DELETE FROM userBank WHERE id = ?";
        connection = new Connector().connect(dbName);
        Connection conn = connection;
        PreparedStatement pstmt = conn.prepareStatement(sql);

        // set the corresponding param
        pstmt.setInt(1, id);
        // execute the delete statement
        pstmt.executeUpdate();
    }

    public User find(int id, String dbName) throws SQLException {
        String sql = "SELECT * FROM userBank WHERE id = ?";
        User user = new User();
        connection = new Connector().connect(dbName);
        Connection conn = connection;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        ResultSet rs = pstmt.executeQuery();
        // loop through the result set
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setPasswordHash(rs.getString("password"));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    }

    public User find(String name, String dbName) throws SQLException {
        String sql = "SELECT * FROM userBank WHERE name = ?";
        User user = new User();
        connection = new Connector().connect(dbName);
        Connection conn = connection;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        // loop through the result set
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setPasswordHash(rs.getString("password"));
        user.setBalance(rs.getBigDecimal("balance"));

        return user;
    }

}
