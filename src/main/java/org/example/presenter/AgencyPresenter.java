package org.example.presenter;

import org.example.model.*;
import org.example.model.repository.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AgencyPresenter {

    private final IAgencyGUI iAgencyGUI;
    private final IHouseRepository houseRepository;
    private final IClientRepository clientRepository;
    private final IRentalRepository rentalRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final String[] HOUSE_COLUMNS = {"ID", "Adresa", "Locatie", "Tip", "Camere", "Pret", "Disponibil"};
    private static final String[] CLIENT_COLUMNS = {"ID", "Nume", "Telefon", "Email"};
    private static final String[] RENTAL_COLUMNS = {"ID", "Tip Locuinta", "Adresa", "Nume Client", "Data Start", "Data End", "Chirie Lunara"};

    public AgencyPresenter(IAgencyGUI iAgencyGUI,
                           IHouseRepository houseRepository,
                           IClientRepository clientRepository,
                           IRentalRepository rentalRepository) {
        this.iAgencyGUI = iAgencyGUI;
        this.houseRepository = houseRepository;
        this.clientRepository = clientRepository;
        this.rentalRepository = rentalRepository;
    }

    // HOUSE CRUD

    public void insertHouse() {
        House house = validHouseInformation();
        if (house == null) return;
        if (houseRepository.addHouse(house)) {
            iAgencyGUI.setMessage("Succes", "Locuinta a fost adaugata cu succes!");
            housesList();
            resetHouseControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la adaugarea locuintei in baza de date!");
        }
    }

    public void updateHouse() {
        int selectedIndex = iAgencyGUI.getSelectedHouseIndex();
        if (selectedIndex < 0) {
            iAgencyGUI.setMessage("Atentie", "Selectati o locuinta din tabel!");
            return;
        }
        House house = validHouseInformation();
        if (house == null) return;
        int id = Integer.parseInt(iAgencyGUI.getHouseIDFromTable(selectedIndex));
        if (houseRepository.updateHouse(id, house)) {
            iAgencyGUI.setMessage("Succes", "Locuinta a fost actualizata cu succes!");
            housesList();
            resetHouseControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la actualizarea locuintei!");
        }
    }

    public void deleteHouse() {
        int selectedIndex = iAgencyGUI.getSelectedHouseIndex();
        if (selectedIndex < 0) {
            iAgencyGUI.setMessage("Atentie", "Selectati o locuinta din tabel!");
            return;
        }
        int id = Integer.parseInt(iAgencyGUI.getHouseIDFromTable(selectedIndex));
        if (houseRepository.deleteHouse(id)) {
            iAgencyGUI.setMessage("Succes", "Locuinta a fost stearsa cu succes!");
            housesList();
            resetHouseControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la stergerea locuintei!");
        }
    }

    public void housesList() {
        List<House> list = houseRepository.houseList();
        setHousesTable(list);
    }

    // HOUSE FILTER

    public void filterHouses() {
        int criterion = iAgencyGUI.getSelectedFilterCriterion();
        String value = iAgencyGUI.getSelectedFilter();

        if (value == null || value.trim().isEmpty()) {
            housesList();
            return;
        }

        List<House> list;
        switch (criterion) {
            case 0:
                list = houseRepository.houseListByLocation(value.trim());
                break;
            case 1:
                try {
                    String[] parts = value.split("-");
                    double min = Double.parseDouble(parts[0].trim());
                    double max = Double.parseDouble(parts[1].trim());
                    list = houseRepository.houseListByPrice(min, max);
                } catch (Exception e) {
                    iAgencyGUI.setMessage("Eroare", "Format pret invalid! Folositi: min-max (ex: 200-500)");
                    return;
                }
                break;
            case 2:
                try {
                    int type = Integer.parseInt(value.trim());
                    list = houseRepository.houseListByType(type);
                } catch (NumberFormatException e) {
                    iAgencyGUI.setMessage("Eroare", "Introduceti un numar valid pentru tip (0-4)!");
                    return;
                }
                break;
            case 3:
                try {
                    int rooms = Integer.parseInt(value.trim());
                    list = houseRepository.houseListByRooms(rooms);
                } catch (NumberFormatException e) {
                    iAgencyGUI.setMessage("Eroare", "Introduceti un numar valid de camere!");
                    return;
                }
                break;
            default:
                housesList();
                return;
        }
        setHousesTable(list);
    }

    // SEARCH HOUSE BY CLIENT

    public void searchHouseByClient() {
        String clientName = iAgencyGUI.getSearchedInformation();
        if (clientName == null || clientName.trim().isEmpty()) {
            iAgencyGUI.setMessage("Atentie", "Introduceti numele clientului!");
            return;
        }

        List<Client> clients = clientRepository.searchClientByName(clientName.trim());
        if (clients.isEmpty()) {
            iAgencyGUI.setMessage("Informare", "Niciun client gasit cu numele: " + clientName);
            return;
        }

        String[] columns = {"Rental ID", "ID Locuinta", "Adresa", "Locatie", "Client", "Data Start", "Data End", "Chirie Lunara"};
        List<String[]> rows = new ArrayList<>();

        for (Client client : clients) {
            List<Rental> rentals = rentalRepository.rentalsByClient(client.getClientID());
            for (Rental rental : rentals) {
                House house = houseRepository.searchHouseByID(rental.getHouseID());
                if (house != null) {
                    rows.add(new String[]{
                            String.valueOf(rental.getRentalID()),
                            String.valueOf(house.getHouseID()),
                            house.getAddress(),
                            house.getLocation(),
                            client.getName(),
                            rental.getStartDate().toString(),
                            rental.getEndDate() != null ? rental.getEndDate().toString() : "Activ",
                            String.valueOf(rental.getMonthlyRent())
                    });
                }
            }
        }

        if (rows.isEmpty()) {
            iAgencyGUI.setMessage("Informare", "Nicio locuinta inchiriata gasita pentru: " + clientName);
        } else {
            iAgencyGUI.setHouseTable(rows, columns);
        }
        iAgencyGUI.resetSearchedInformation();
    }

    //  HOUSE TABLE SELECTION

    public void setHouseControls() {
        int selectedIndex = iAgencyGUI.getSelectedHouseIndex();
        if (selectedIndex < 0) return;

        int id = Integer.parseInt(iAgencyGUI.getHouseIDFromTable(selectedIndex));
        House house = houseRepository.searchHouseByID(id);
        if (house != null) {
            iAgencyGUI.setHouseID(house.getHouseID());
            iAgencyGUI.setHouseAddress(house.getAddress());
            iAgencyGUI.setHouseLocation(house.getLocation());

            // Folosim String în loc de index (MVP clean)
            iAgencyGUI.setHouseTypeName(houseTypeRo(house.getHouseType()));

            iAgencyGUI.setNumberOfRooms(house.getNumberOfRooms());
            iAgencyGUI.setHousePrice(house.getPrice());
            iAgencyGUI.setHouseImages(
                    nvl(house.getImage1()),
                    nvl(house.getImage2()),
                    nvl(house.getImage3())
            );
        }
    }

    //  CLIENT CRUD

    public void insertClient() {
        Client client = validClientInformation();
        if (client == null) return;
        if (clientRepository.addClient(client)) {
            iAgencyGUI.setMessage("Succes", "Clientul a fost adaugat cu succes!");
            clientsList();
            resetClientControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la adaugarea clientului in baza de date!");
        }
    }

    public void updateClient() {
        int selectedIndex = iAgencyGUI.getSelectedClientIndex();
        if (selectedIndex < 0) {
            iAgencyGUI.setMessage("Atentie", "Selectati un client din tabel!");
            return;
        }
        Client client = validClientInformation();
        if (client == null) return;
        int id = Integer.parseInt(iAgencyGUI.getClientIDFromTable(selectedIndex));
        if (clientRepository.updateClient(id, client)) {
            iAgencyGUI.setMessage("Succes", "Clientul a fost actualizat cu succes!");
            clientsList();
            resetClientControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la actualizarea clientului!");
        }
    }

    public void deleteClient() {
        int selectedIndex = iAgencyGUI.getSelectedClientIndex();
        if (selectedIndex < 0) {
            iAgencyGUI.setMessage("Atentie", "Selectati un client din tabel!");
            return;
        }
        int id = Integer.parseInt(iAgencyGUI.getClientIDFromTable(selectedIndex));
        if (clientRepository.deleteClient(id)) {
            iAgencyGUI.setMessage("Succes", "Clientul a fost sters cu succes!");
            clientsList();
            resetClientControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la stergerea clientului!");
        }
    }

    public void clientsList() {
        List<Client> list = clientRepository.clientList();
        List<String[]> rows = new ArrayList<>();
        for (Client c : list) {
            rows.add(new String[]{
                    String.valueOf(c.getClientID()),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail()
            });
        }
        iAgencyGUI.setClientTable(rows, CLIENT_COLUMNS);
    }

    public void setClientControls() {
        int selectedIndex = iAgencyGUI.getSelectedClientIndex();
        if (selectedIndex < 0) return;
        int id = Integer.parseInt(iAgencyGUI.getClientIDFromTable(selectedIndex));
        Client client = clientRepository.searchClientByID(id);
        if (client != null) {
            iAgencyGUI.setClientID(client.getClientID());
            iAgencyGUI.setClientName(client.getName());
            iAgencyGUI.setClientPhone(client.getPhone());
            iAgencyGUI.setClientEmail(client.getEmail());
        }
    }

    //  RENTAL CRUD

    public void insertRental() {
        Rental rental = validRentalInformation();
        if (rental == null) return;

        if (rentalRepository.addRental(rental)) {
            iAgencyGUI.setMessage("Succes", "Contractul de inchiriere a fost adaugat cu succes!");

            rentalsList();
            updateHouseAvailability(rental.getHouseID());
            housesList();
            resetRentalControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la adaugarea contractului de inchiriere!");
        }
    }

    public void updateRental() {
        int selectedIndex = iAgencyGUI.getSelectedRentalIndex();
        if (selectedIndex < 0) {
            iAgencyGUI.setMessage("Atentie", "Selectati un contract din tabel!");
            return;
        }

        Rental rental = validRentalInformation();
        if (rental == null) return;

        int rentalId = Integer.parseInt(iAgencyGUI.getRentalIDFromTable(selectedIndex));

        if (rentalRepository.updateRental(rentalId, rental)) {
            iAgencyGUI.setMessage("Succes", "Contractul de inchiriere a fost actualizat cu succes!");

            rentalsList();
            updateHouseAvailability(rental.getHouseID());
            housesList();
            resetRentalControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la actualizarea contractului de inchiriere!");
        }
    }

    public void deleteRental() {
        int selectedIndex = iAgencyGUI.getSelectedRentalIndex();
        if (selectedIndex < 0) {
            iAgencyGUI.setMessage("Atentie", "Selectati un contract din tabel!");
            return;
        }

        int rentalId = Integer.parseInt(iAgencyGUI.getRentalIDFromTable(selectedIndex));


        Rental rentalToDelete = rentalRepository.rentalList().stream()
                .filter(r -> r.getRentalID() == rentalId)
                .findFirst()
                .orElse(null);

        int houseId = (rentalToDelete != null) ? rentalToDelete.getHouseID() : -1;

        if (rentalRepository.deleteRental(rentalId)) {
            iAgencyGUI.setMessage("Succes", "Contractul de inchiriere a fost sters cu succes!");

            rentalsList();
            if (houseId > 0) {
                updateHouseAvailability(houseId);
            }
            housesList();
            resetRentalControls();
        } else {
            iAgencyGUI.setMessage("Eroare", "Eroare la stergerea contractului de inchiriere!");
        }
    }

    public void rentalsList() {
        List<Rental> list = rentalRepository.rentalList();
        List<String[]> rows = new ArrayList<>();
        for (Rental r : list) {
            Client client = clientRepository.searchClientByID(r.getClientID());
            House house = houseRepository.searchHouseByID(r.getHouseID());
            String clientName = client != null ? client.getName() : "ID: " + r.getClientID();
            String houseAddr = house != null ? house.getAddress() : "ID: " + r.getHouseID();
            String houseType = house != null ? house.getHouseType().name() : "-";

            rows.add(new String[]{
                    String.valueOf(r.getRentalID()),
                    houseType,
                    houseAddr,
                    clientName,
                    r.getStartDate().toString(),
                    r.getEndDate() != null ? r.getEndDate().toString() : "Activ",
                    String.valueOf(r.getMonthlyRent())
            });
        }
        iAgencyGUI.setRentalTable(rows, RENTAL_COLUMNS);
    }

    public void setRentalControls() {
        int selectedIndex = iAgencyGUI.getSelectedRentalIndex();
        if (selectedIndex < 0) return;

        int id = Integer.parseInt(iAgencyGUI.getRentalIDFromTable(selectedIndex));

        List<Rental> all = rentalRepository.rentalList();
        Rental rental = all.stream()
                .filter(r -> r.getRentalID() == id)
                .findFirst()
                .orElse(null);

        if (rental != null) {
            iAgencyGUI.setRentalID(rental.getRentalID());
            iAgencyGUI.setRentalHouseID(String.valueOf(rental.getHouseID()));
            iAgencyGUI.setRentalClientID(String.valueOf(rental.getClientID()));
            iAgencyGUI.setRentalStartDate(rental.getStartDate().toString());
            iAgencyGUI.setRentalEndDate(rental.getEndDate() != null ? rental.getEndDate().toString() : "");
            iAgencyGUI.setRentalMonthlyRent(rental.getMonthlyRent());
        }
    }

    //  VALIDATION

    private House validHouseInformation() {
        String address = iAgencyGUI.getHouseAddress();
        String location = iAgencyGUI.getHouseLocation();
        double price = iAgencyGUI.getHousePrice();
        int rooms = iAgencyGUI.getNumberOfRooms();
        String typeName = iAgencyGUI.getHouseTypeName();

        if (address == null || address.trim().isEmpty()) {
            iAgencyGUI.setMessage("Eroare", "Introduceti adresa locuintei!");
            return null;
        }
        if (location == null || location.trim().isEmpty()) {
            iAgencyGUI.setMessage("Eroare", "Introduceti locatia locuintei!");
            return null;
        }
        if (price <= 0) {
            iAgencyGUI.setMessage("Eroare", "Pretul trebuie sa fie mai mare decat 0!");
            return null;
        }
        if (rooms <= 0) {
            iAgencyGUI.setMessage("Eroare", "Numarul de camere trebuie sa fie mai mare decat 0!");
            return null;
        }

        House house = new House();
        house.setAddress(address.trim());
        house.setLocation(location.trim());
        house.setHouseType(HouseType.fromName(typeName));
        house.setNumberOfRooms(rooms);
        house.setPrice(price);
        house.setAvailable(true);
        return house;
    }

    private Client validClientInformation() {
        String name = iAgencyGUI.getClientName();
        String phone = iAgencyGUI.getClientPhone();
        String email = iAgencyGUI.getClientEmail();

        if (name == null || name.trim().isEmpty()) {
            iAgencyGUI.setMessage("Eroare", "Introduceti numele clientului!");
            return null;
        }
        if (phone == null || phone.trim().isEmpty()) {
            iAgencyGUI.setMessage("Eroare", "Introduceti telefonul clientului!");
            return null;
        }
        if (email == null || email.trim().isEmpty()) {
            iAgencyGUI.setMessage("Eroare", "Introduceti emailul clientului!");
            return null;
        }

        Client client = new Client();
        client.setName(name.trim());
        client.setPhone(phone.trim());
        client.setEmail(email.trim());
        return client;
    }

    private Rental validRentalInformation() {
        // ... (această metodă rămâne neschimbată)
        String houseIDStr = iAgencyGUI.getRentalHouseID();
        String clientIDStr = iAgencyGUI.getRentalClientID();
        String startStr = iAgencyGUI.getRentalStartDate();
        String endStr = iAgencyGUI.getRentalEndDate();
        double rent = iAgencyGUI.getRentalMonthlyRent();

        int houseID, clientID;
        try {
            houseID = Integer.parseInt(houseIDStr.trim());
        } catch (NumberFormatException e) {
            iAgencyGUI.setMessage("Eroare", "ID Locuinta trebuie sa fie un numar intreg valid!");
            return null;
        }
        try {
            clientID = Integer.parseInt(clientIDStr.trim());
        } catch (NumberFormatException e) {
            iAgencyGUI.setMessage("Eroare", "ID Client trebuie sa fie un numar intreg valid!");
            return null;
        }

        if (houseRepository.searchHouseByID(houseID) == null) {
            iAgencyGUI.setMessage("Eroare", "Nu exista nicio locuinta cu ID-ul: " + houseID);
            return null;
        }
        if (clientRepository.searchClientByID(clientID) == null) {
            iAgencyGUI.setMessage("Eroare", "Nu exista niciun client cu ID-ul: " + clientID);
            return null;
        }

        LocalDate startDate;
        try {
            startDate = LocalDate.parse(startStr.trim(), DATE_FORMAT);
        } catch (DateTimeParseException e) {
            iAgencyGUI.setMessage("Eroare", "Data de start invalida! Folositi formatul: yyyy-MM-dd");
            return null;
        }

        LocalDate endDate = null;
        if (endStr != null && !endStr.trim().isEmpty()) {
            try {
                endDate = LocalDate.parse(endStr.trim(), DATE_FORMAT);
                if (endDate.isBefore(startDate)) {
                    iAgencyGUI.setMessage("Eroare", "Data de sfarsit nu poate fi inainte de data de start!");
                    return null;
                }
            } catch (DateTimeParseException e) {
                iAgencyGUI.setMessage("Eroare", "Data de sfarsit invalida! Folositi formatul: yyyy-MM-dd");
                return null;
            }
        }

        if (rent <= 0) {
            iAgencyGUI.setMessage("Eroare", "Chiria lunara trebuie sa fie mai mare decat 0!");
            return null;
        }

        Rental rental = new Rental();
        rental.setHouseID(houseID);
        rental.setClientID(clientID);
        rental.setStartDate(startDate);
        rental.setEndDate(endDate);
        rental.setMonthlyRent(rent);
        return rental;
    }

    //  HOUSE AVAILABILITY LOGIC

    private void updateHouseAvailability(int houseId) {
        House house = houseRepository.searchHouseByID(houseId);
        if (house == null) return;

        boolean shouldBeAvailable = isHouseCurrentlyAvailable(houseId);

        if (house.isAvailable() != shouldBeAvailable) {
            house.setAvailable(shouldBeAvailable);
            houseRepository.updateHouse(houseId, house);
        }
    }

    private boolean isHouseCurrentlyAvailable(int houseId) {
        List<Rental> rentals = rentalRepository.getRentalsByHouse(houseId);

        LocalDate today = LocalDate.now();

        for (Rental r : rentals) {
            if (r.getEndDate() == null || r.getEndDate().isAfter(today)) {
                return false;
            }
        }
        return true;
    }

    // RESET CONTROLS

    private void resetHouseControls() {
        iAgencyGUI.setHouseID(0);
        iAgencyGUI.setHouseAddress("");
        iAgencyGUI.setHouseLocation("");
        iAgencyGUI.setHouseTypeName("");
        iAgencyGUI.setNumberOfRooms(0);
        iAgencyGUI.setHousePrice(0);
        iAgencyGUI.setHouseImages("", "", "");
    }

    private void resetClientControls() {
        iAgencyGUI.setClientID(0);
        iAgencyGUI.setClientName("");
        iAgencyGUI.setClientPhone("");
        iAgencyGUI.setClientEmail("");
    }

    private void resetRentalControls() {
        iAgencyGUI.setRentalID(0);
        iAgencyGUI.setRentalHouseID("");
        iAgencyGUI.setRentalClientID("");
        iAgencyGUI.setRentalStartDate("");
        iAgencyGUI.setRentalEndDate("");
        iAgencyGUI.setRentalMonthlyRent(0);
    }

    // HELPER METHODS

    private void setHousesTable(List<House> list) {
        List<String[]> rows = new ArrayList<>();
        for (House h : list) {
            rows.add(new String[]{
                    String.valueOf(h.getHouseID()),
                    h.getAddress(),
                    h.getLocation(),
                    houseTypeRo(h.getHouseType()),
                    String.valueOf(h.getNumberOfRooms()),
                    String.valueOf(h.getPrice()),
                    h.isAvailable() ? "Da" : "Nu"
            });
        }
        iAgencyGUI.setHouseTable(rows, HOUSE_COLUMNS);
    }

    private String houseTypeRo(HouseType type) {
        switch (type) {
            case Apartment: return "Apartament";
            case Studio: return "Garsoniera";
            case House: return "Casa";
            case Villa: return "Vila";
            case Penthouse: return "Penthouse";
            default: return type.name();
        }
    }

    private String nvl(String value) {
        return value != null ? value : "";
    }
}