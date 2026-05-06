package org.example.model;

public class Client {
    private int clientID;
    private String name;
    private String phone;
    private String email;

    public Client() {
    }

    public Client(int clientID, String name, String phone, String email) {
        this.clientID = clientID;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public Client(Client client) {
        this.clientID = client.clientID;
        this.name = client.name;
        this.phone = client.phone;
        this.email = client.email;
    }


    public int getClientID() { return clientID; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }


    public void setClientID(int clientID) { this.clientID = clientID; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Client{" +
                "ID=" + clientID +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
