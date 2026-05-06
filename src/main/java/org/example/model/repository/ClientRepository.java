package org.example.model.repository;

import org.example.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository implements IClientRepository {
    private final DatabaseConnection dbConnection;

    public ClientRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean addClient(Client client) {
        String sql = "INSERT INTO Client (Name, Phone, Email) VALUES (?, ?, ?)";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getPhone());
            ps.setString(3, client.getEmail());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL addClient: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteClient(int clientID) {
        String sql = "DELETE FROM Client WHERE ID = ?";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, clientID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL deleteClient: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateClient(int clientID, Client client) {
        String sql = "UPDATE Client SET Name=?, Phone=?, Email=? WHERE ID=?";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, client.getName());
            ps.setString(2, client.getPhone());
            ps.setString(3, client.getEmail());
            ps.setInt(4, clientID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL updateClient: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Client> clientList() {
        return executeQuery("SELECT * FROM Client ORDER BY Name");
    }

    @Override
    public Client searchClientByID(int id) {
        List<Client> list = executeQuery("SELECT * FROM Client WHERE ID = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Client> searchClientByName(String name) {
        return executeQuery("SELECT * FROM Client WHERE Name LIKE ?", "%" + name + "%");
    }


    private List<Client> executeQuery(String sql, Object... params) {
        List<Client> list = new ArrayList<>();
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(convertToClient(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citire Client: " + e.getMessage());
        }
        return list;
    }

    private Client convertToClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("ID"),
                rs.getString("Name"),
                rs.getString("Phone"),
                rs.getString("Email")
        );
    }
}