package org.example.model.repository;

import org.example.model.House;
import org.example.model.HouseType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HouseRepository implements IHouseRepository {
    private final DatabaseConnection dbConnection;

    public HouseRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean addHouse(House house) {
        String sql = "INSERT INTO House (Address, Location, HouseType, NumberOfRooms, Price, "
                + "Image1, Image2, Image3, Available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, house.getAddress());
            ps.setString(2, house.getLocation());
            ps.setInt(3, house.getHouseType().ordinal());
            ps.setInt(4, house.getNumberOfRooms());
            ps.setDouble(5, house.getPrice());
            ps.setString(6, nvl(house.getImage1()));
            ps.setString(7, nvl(house.getImage2()));
            ps.setString(8, nvl(house.getImage3()));
            ps.setBoolean(9, house.isAvailable());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL addHouse: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteHouse(int houseID) {
        String sql = "DELETE FROM House WHERE ID = ?";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, houseID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL deleteHouse: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateHouse(int houseID, House house) {
        String sql = "UPDATE House SET Address=?, Location=?, HouseType=?, NumberOfRooms=?, "
                + "Price=?, Image1=?, Image2=?, Image3=?, Available=? WHERE ID=?";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, house.getAddress());
            ps.setString(2, house.getLocation());
            ps.setInt(3, house.getHouseType().ordinal());
            ps.setInt(4, house.getNumberOfRooms());
            ps.setDouble(5, house.getPrice());
            ps.setString(6, nvl(house.getImage1()));
            ps.setString(7, nvl(house.getImage2()));
            ps.setString(8, nvl(house.getImage3()));
            ps.setBoolean(9, house.isAvailable());
            ps.setInt(10, houseID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL updateHouse: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<House> houseList() {
        return executeQuery("SELECT * FROM House ORDER BY Location, Price");
    }

    @Override
    public List<House> houseListByLocation(String location) {
        return executeQuery("SELECT * FROM House WHERE Location LIKE ?", "%" + location + "%");
    }

    @Override
    public List<House> houseListByType(int type) {
        return executeQuery("SELECT * FROM House WHERE HouseType = ?", type);
    }

    @Override
    public List<House> houseListByPrice(double minPrice, double maxPrice) {
        return executeQuery("SELECT * FROM House WHERE Price BETWEEN ? AND ?", minPrice, maxPrice);
    }

    @Override
    public List<House> houseListByRooms(int rooms) {
        return executeQuery("SELECT * FROM House WHERE NumberOfRooms = ?", rooms);
    }

    @Override
    public House searchHouseByID(int id) {
        List<House> list = executeQuery("SELECT * FROM House WHERE ID = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<House> searchHouseByAddress(String address) {
        return executeQuery("SELECT * FROM House WHERE Address LIKE ?", "%" + address + "%");
    }



    private List<House> executeQuery(String sql, Object... params) {
        List<House> list = new ArrayList<>();
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(convertToHouse(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citire House: " + e.getMessage());
        }
        return list;
    }

    private House convertToHouse(ResultSet rs) throws SQLException {
        return new House(
                rs.getInt("ID"),
                rs.getString("Address"),
                rs.getString("Location"),
                HouseType.fromInt(rs.getInt("HouseType")),
                rs.getInt("NumberOfRooms"),
                rs.getDouble("Price"),
                rs.getString("Image1"),
                rs.getString("Image2"),
                rs.getString("Image3"),
                rs.getBoolean("Available")
        );
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }
}