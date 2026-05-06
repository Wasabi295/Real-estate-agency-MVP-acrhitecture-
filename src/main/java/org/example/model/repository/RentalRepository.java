package org.example.model.repository;

import org.example.model.Rental;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalRepository implements IRentalRepository {
    private final DatabaseConnection dbConnection;

    public RentalRepository(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public boolean addRental(Rental rental) {
        String sql = "INSERT INTO Rental (HouseID, ClientID, StartDate, EndDate, MonthlyRent) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rental.getHouseID());
            ps.setInt(2, rental.getClientID());
            ps.setDate(3, Date.valueOf(rental.getStartDate()));
            if (rental.getEndDate() != null) {
                ps.setDate(4, Date.valueOf(rental.getEndDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setDouble(5, rental.getMonthlyRent());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL addRental: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteRental(int rentalID) {
        String sql = "DELETE FROM Rental WHERE ID = ?";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rentalID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL deleteRental: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateRental(int rentalID, Rental rental) {
        String sql = "UPDATE Rental SET HouseID=?, ClientID=?, StartDate=?, EndDate=?, "
                + "MonthlyRent=? WHERE ID=?";
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rental.getHouseID());
            ps.setInt(2, rental.getClientID());
            ps.setDate(3, Date.valueOf(rental.getStartDate()));
            if (rental.getEndDate() != null) {
                ps.setDate(4, Date.valueOf(rental.getEndDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.setDouble(5, rental.getMonthlyRent());
            ps.setInt(6, rentalID);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Eroare SQL updateRental: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Rental> rentalList() {
        return executeQuery("SELECT * FROM Rental ORDER BY StartDate DESC");
    }

    @Override
    public List<Rental> rentalsByClient(int clientID) {
        return executeQuery("SELECT * FROM Rental WHERE ClientID = ?", clientID);
    }



    private List<Rental> executeQuery(String sql, Object... params) {
        List<Rental> list = new ArrayList<>();
        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(convertToRental(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citire Rental: " + e.getMessage());
        }
        return list;
    }

    private Rental convertToRental(ResultSet rs) throws SQLException {
        LocalDate endDate = null;
        Date sqlEnd = rs.getDate("EndDate");
        if (sqlEnd != null) {
            endDate = sqlEnd.toLocalDate();
        }
        return new Rental(
                rs.getInt("ID"),
                rs.getInt("HouseID"),
                rs.getInt("ClientID"),
                rs.getDate("StartDate").toLocalDate(),
                endDate,
                rs.getDouble("MonthlyRent")
        );
    }

    @Override
    public List<Rental> getRentalsByHouse(int houseId) {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM Rental WHERE HouseID = ?";

        try (Connection conn = dbConnection.openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, houseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rentals.add(convertToRental(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Eroare la citire getRentalsByHouse: " + e.getMessage());
        }
        return rentals;
    }


}