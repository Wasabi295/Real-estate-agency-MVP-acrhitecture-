package org.example.presenter;

import java.util.List;

public interface IAgencyGUI {


    int getHouseID();
    void setHouseID(int id);
    String getHouseAddress();
    void setHouseAddress(String address);
    String getHouseLocation();
    void setHouseLocation(String location);
    String getHouseTypeName();
    void setHouseTypeName(String houseTypeName);
    int getNumberOfRooms();
    void setNumberOfRooms(int rooms);
    double getHousePrice();
    void setHousePrice(double price);
    void setHouseImages(String img1, String img2, String img3);


    void setHouseTable(List<String[]> rows, String[] columns);
    int getSelectedHouseIndex();
    String getHouseIDFromTable(int index);


    int getClientID();
    void setClientID(int id);
    String getClientName();
    void setClientName(String name);
    String getClientPhone();
    void setClientPhone(String phone);
    String getClientEmail();
    void setClientEmail(String email);


    void setClientTable(List<String[]> rows, String[] columns);
    int getSelectedClientIndex();
    String getClientIDFromTable(int index);


    int getRentalID();
    void setRentalID(int id);
    String getRentalHouseID();
    void setRentalHouseID(String houseID);
    String getRentalClientID();
    void setRentalClientID(String clientID);
    String getRentalStartDate();
    void setRentalStartDate(String date);
    String getRentalEndDate();
    void setRentalEndDate(String date);
    double getRentalMonthlyRent();
    void setRentalMonthlyRent(double rent);


    void setRentalTable(List<String[]> rows, String[] columns);
    int getSelectedRentalIndex();
    String getRentalIDFromTable(int index);


    String getSearchedInformation();
    void resetSearchedInformation();
    String getSelectedFilter();
    void resetSelectedFilter();
    int getSelectedFilterCriterion();
    void resetSelectedFilterCriterion();


    void setVisibility(boolean visibility);
    void setMessage(String title, String message);
}