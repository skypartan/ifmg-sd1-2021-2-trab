package br.edu.ifmg.sdtrab.storage;

import br.edu.ifmg.sdtrab.entity.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private Connection connection;

    public UserDao() throws IOException, SQLException {
        connection = new ConnectionBuilder().build();
    }

    public boolean save(User user) throws SQLException {
        var sql = "insert into user (name, password, balance) values (?, ?, ?);";
        var statement = connection.prepareStatement(sql);
        statement.setString(1, user.getName());
        statement.setString(2, user.getPasswordHash());
        statement.setBigDecimal(3, user.getBalance());
        return statement.execute();
    }

    public boolean delete(User user) throws SQLException {
        var sql = "delete from user where id = ?;";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, user.getId());
        return statement.executeUpdate() > 0;
    }

    public User find(int id) throws SQLException {
        var sql = "select * from user where id = ?;";
        var statement = connection.prepareStatement(sql);
        statement.setInt(1, id);

        var result = statement.executeQuery();
        if (!result.first())
            return null;

        var user = new User();
        user.setId(result.getInt(1));
        user.setName(result.getString(2));
        user.setPasswordHash(result.getString(3));
        user.setBalance(result.getBigDecimal(4));
        return user;
    }

    public List<User> search(String query) throws SQLException {
        var sql = "select * from user ?;";
        var statement = connection.prepareStatement(sql);
        statement.setString(1, query);

        var result = statement.executeQuery();
        var list = new ArrayList<User>();

        while (result.next()) {
            var user = new User();
            user.setId(result.getInt(1));
            user.setName(result.getString(2));
            user.setPasswordHash(result.getString(3));
            user.setBalance(result.getBigDecimal(4));
            list.add(user);
        }

        return list;
    }
}
