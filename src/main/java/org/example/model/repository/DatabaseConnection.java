package org.example.model.repository;

import java.sql.*;

public class DatabaseConnection {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}