package org.example.model.repository;

import org.example.model.Rental;
import java.util.List;

public interface IRentalRepository {
    boolean addRental(Rental rental);
    boolean deleteRental(int rentalID);
    boolean updateRental(int rentalID, Rental rental);
    List<Rental> rentalList();
    List<Rental> rentalsByClient(int clientID);
    List<Rental> getRentalsByHouse(int houseId);
}