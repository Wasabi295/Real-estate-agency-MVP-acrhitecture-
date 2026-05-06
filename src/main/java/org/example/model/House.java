package org.example.model;

public class House {
    private int houseID;
    private String address;
    private String location;
    private HouseType houseType;
    private int numberOfRooms;
    private double price;
    private String image1;
    private String image2;
    private String image3;
    private boolean available;

    public House() {}

    public House(int houseID, String address, String location, HouseType houseType,
                 int numberOfRooms, double price, String image1, String image2,
                 String image3, boolean available) {
        this.houseID = houseID;
        this.address = address;
        this.location = location;
        this.houseType = houseType;
        this.numberOfRooms = numberOfRooms;
        this.price = price;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.available = available;
    }

    public House(House house) {
        this(house.houseID, house.address, house.location, house.houseType,
                house.numberOfRooms, house.price, house.image1, house.image2,
                house.image3, house.available);
    }

    public int getHouseID() { return houseID; }
    public String getAddress() { return address; }
    public String getLocation() { return location; }
    public HouseType getHouseType() { return houseType; }
    public int getNumberOfRooms() { return numberOfRooms; }
    public double getPrice() { return price; }
    public String getImage1() { return image1; }
    public String getImage2() { return image2; }
    public String getImage3() { return image3; }
    public boolean isAvailable() { return available; }

    public void setHouseID(int houseID) { this.houseID = houseID; }
    public void setAddress(String address) { this.address = address; }
    public void setLocation(String location) { this.location = location; }
    public void setHouseType(HouseType houseType) { this.houseType = houseType; }
    public void setNumberOfRooms(int numberOfRooms) { this.numberOfRooms = numberOfRooms; }
    public void setPrice(double price) { this.price = price; }
    public void setImage1(String image1) { this.image1 = image1; }
    public void setImage2(String image2) { this.image2 = image2; }
    public void setImage3(String image3) { this.image3 = image3; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return "House{ID=" + houseID + ", address='" + address + "', location='" + location
                + "', type=" + houseType + ", rooms=" + numberOfRooms + ", price=" + price
                + ", available=" + available + '}';
    }
}
