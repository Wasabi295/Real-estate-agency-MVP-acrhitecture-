package org.example.model;

import java.time.LocalDate;

public class Rental {
    private int rentalID;
    private int houseID;
    private int clientID;
    private LocalDate startDate;
    private LocalDate endDate;
    private double monthlyRent;

    public Rental() {}

    public Rental(int rentalID, int houseID, int clientID, LocalDate startDate,
                  LocalDate endDate, double monthlyRent) {
        this.rentalID = rentalID;
        this.houseID = houseID;
        this.clientID = clientID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthlyRent = monthlyRent;
    }

    public int getRentalID() { return rentalID; }
    public int getHouseID() { return houseID; }
    public int getClientID() { return clientID; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getMonthlyRent() { return monthlyRent; }

    public void setRentalID(int rentalID) { this.rentalID = rentalID; }
    public void setHouseID(int houseID) { this.houseID = houseID; }
    public void setClientID(int clientID) { this.clientID = clientID; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setMonthlyRent(double monthlyRent) { this.monthlyRent = monthlyRent; }

    @Override
    public String toString() {
        return "Rental{ID=" + rentalID + ", houseID=" + houseID + ", clientID=" + clientID
                + ", start=" + startDate + ", end=" + endDate + ", rent=" + monthlyRent + '}';
    }
}
