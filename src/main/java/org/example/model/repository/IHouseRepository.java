package org.example.model.repository;

import org.example.model.House;
import java.util.List;

public interface IHouseRepository {
    boolean addHouse(House house);
    boolean deleteHouse(int houseID);
    boolean updateHouse(int houseID, House house);
    List<House> houseList();
    List<House> houseListByLocation(String location);
    List<House> houseListByType(int type);
    List<House> houseListByPrice(double minPrice, double maxPrice);
    List<House> houseListByRooms(int rooms);
    House searchHouseByID(int id);
    List<House> searchHouseByAddress(String address);
}

