package br.edu.ifmg.sdtrab.storage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionBuilder {

    private StorageProperties properties;

    public ConnectionBuilder() throws IOException {
        properties = new StorageProperties("storage.properties");
    }



    public Connection build() throws SQLException {
        return DriverManager.getConnection(String.format("jdbc:mariadb://%s:%d/%s?user=%s&password=%s",
                properties.getDbHost(), properties.getDbPort(), properties.getDbName(),
                properties.getDbUser(), properties.getDbPassword()));
    }

    private static class StorageProperties {

        private String dbHost;
        private int dbPort;
        private String dbUser;
        private String dbPassword;
        private String dbName;

        public StorageProperties(String file) throws IOException {
            var properties = new Properties();
            properties.load(new FileInputStream(file));

            dbHost = properties.getProperty("DB_HOST");
            dbPort = Integer.parseInt(properties.getProperty("DB_PORT"));
            dbUser = properties.getProperty("DB_USER");
            dbPassword = properties.getProperty("DB_PASSWORD");
            dbName = properties.getProperty("DB_NAME");
        }

        public String getDbHost() {
            return dbHost;
        }

        public void setDbHost(String dbHost) {
            this.dbHost = dbHost;
        }

        public int getDbPort() {
            return dbPort;
        }

        public void setDbPort(int dbPort) {
            this.dbPort = dbPort;
        }

        public String getDbUser() {
            return dbUser;
        }

        public void setDbUser(String dbUser) {
            this.dbUser = dbUser;
        }

        public String getDbPassword() {
            return dbPassword;
        }

        public void setDbPassword(String dbPassword) {
            this.dbPassword = dbPassword;
        }

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }
    }
}
