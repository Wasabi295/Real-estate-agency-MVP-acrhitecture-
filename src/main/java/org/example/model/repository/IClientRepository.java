package org.example.model.repository;

import org.example.model.Client;
import java.util.List;

public interface IClientRepository {
    boolean addClient(Client client);
    boolean deleteClient(int clientID);
    boolean updateClient(int clientID, Client client);
    List<Client> clientList();
    Client searchClientByID(int id);
    List<Client> searchClientByName(String name);
}
