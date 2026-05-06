package org.example.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.presenter.AgencyPresenter;
import org.example.presenter.IAgencyGUI;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JavaFXGUI implements IAgencyGUI {

    private AgencyPresenter agencyPresenter;
    private final Stage stage;

    //  HOUSE FORM FIELDS
    private TextField txtHouseID;
    private TextField txtAddress;
    private TextField txtLocation;
    private ComboBox<String> cmbHouseType;
    private TextField txtRooms;
    private TextField txtPrice;

    //  HOUSE IMAGE DISPLAY
    private ImageView imgView1;
    private ImageView imgView2;
    private ImageView imgView3;

    // TABS
    private Tab tabHouses;
    private Tab tabClients;
    private Tab tabRentals;

    // HOUSE TABLE
    private TableView<Map<String, String>> tblHouses;
    private ObservableList<Map<String, String>> houseTableData;
    private String[] currentHouseColumns;

    // HOUSE SEARCH & FILTER
    private TextField txtSearch;
    private ComboBox<String> cmbFilterCriterion;
    private TextField txtFilterValue;

    // CLIENT FORM FIELDS
    private TextField txtClientID;
    private TextField txtClientName;
    private TextField txtClientPhone;
    private TextField txtClientEmail;

    //  CLIENT TABLE
    private TableView<Map<String, String>> tblClients;
    private ObservableList<Map<String, String>> clientTableData;
    private String[] currentClientColumns;

    //  RENTAL FORM FIELDS
    private TextField txtRentalID;
    private TextField txtRentalHouseID;
    private TextField txtRentalClientID;
    private TextField txtRentalStartDate;
    private TextField txtRentalEndDate;
    private TextField txtRentalMonthlyRent;

    // RENTAL TABLE
    private TableView<Map<String, String>> tblRentals;
    private ObservableList<Map<String, String>> rentalTableData;
    private String[] currentRentalColumns;

    public JavaFXGUI(Stage stage) {
        this.stage = stage;
        this.houseTableData = FXCollections.observableArrayList();
        this.clientTableData = FXCollections.observableArrayList();
        this.rentalTableData = FXCollections.observableArrayList();
        this.currentHouseColumns = new String[]{};
        this.currentClientColumns = new String[]{};
        this.currentRentalColumns = new String[]{};
        initComponents();
    }

    public void setPresenter(AgencyPresenter presenter) {
        this.agencyPresenter = presenter;
        attachEventHandlers();
    }


    private void initComponents() {
        stage.setTitle("Agentie Imobiliara - Gestiune Inchirieri");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabHouses = new Tab("Locuinte", buildHousesTab());
        tabClients = new Tab("Clienti", buildClientsTab());
        tabRentals = new Tab("Contracte", buildRentalsTab());

        tabPane.getTabs().addAll(tabHouses, tabClients, tabRentals);


        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (agencyPresenter == null) return;
            if (newTab == tabClients) agencyPresenter.clientsList();
            if (newTab == tabRentals) agencyPresenter.rentalsList();
            if (newTab == tabHouses) agencyPresenter.housesList();
        });

        Scene scene = new Scene(tabPane, 1100, 780);
        stage.setScene(scene);
    }

    // TAB 1: LOCUINTE
    private VBox buildHousesTab() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(10));


        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        txtHouseID = new TextField();
        txtHouseID.setEditable(false);
        txtHouseID.setPrefWidth(80);

        txtAddress = new TextField();
        txtAddress.setPrefWidth(260);

        txtLocation = new TextField();
        txtLocation.setPrefWidth(200);


        cmbHouseType = new ComboBox<>(FXCollections.observableArrayList(
                "Apartament", "Garsoniera", "Casa", "Vila", "Penthouse"
        ));
        cmbHouseType.getSelectionModel().selectFirst();

        txtRooms = new TextField();
        txtRooms.setPrefWidth(80);

        txtPrice = new TextField();
        txtPrice.setPrefWidth(120);

        grid.add(new Label("ID:"), 0, 0);
        grid.add(txtHouseID, 1, 0);
        grid.add(new Label("Adresa:"), 0, 1);
        grid.add(txtAddress, 1, 1);
        grid.add(new Label("Locatie:"), 0, 2);
        grid.add(txtLocation, 1, 2);
        grid.add(new Label("Tip:"), 0, 3);
        grid.add(cmbHouseType, 1, 3);
        grid.add(new Label("Nr. Camere:"), 2, 0);
        grid.add(txtRooms, 3, 0);
        grid.add(new Label("Pret (EUR):"), 2, 1);
        grid.add(txtPrice, 3, 1);

        Button btnAdd = styledButton("Adauga", "#4CAF50");
        Button btnUpdate = styledButton("Actualizeaza", "#2196F3");
        Button btnDelete = styledButton("Sterge", "#F44336");

        btnAdd.setOnAction(e -> agencyPresenter.insertHouse());
        btnUpdate.setOnAction(e -> agencyPresenter.updateHouse());
        btnDelete.setOnAction(e -> agencyPresenter.deleteHouse());

        HBox btnBox = new HBox(10, btnAdd, btnUpdate, btnDelete);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 2, 2, 2, 1);

        TitledPane infoPane = new TitledPane("INFORMATII LOCUINTA", grid);
        infoPane.setCollapsible(false);

        //  Image panel
        imgView1 = buildImageView();
        imgView2 = buildImageView();
        imgView3 = buildImageView();
        HBox imgBox = new HBox(10, imgView1, imgView2, imgView3);
        imgBox.setAlignment(Pos.CENTER);
        imgBox.setPadding(new Insets(6));
        imgBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 4;");
        TitledPane imgPane = new TitledPane("IMAGINI LOCUINTA", imgBox);
        imgPane.setCollapsible(true);
        imgPane.setExpanded(false);

        //  Table
        tblHouses = buildTable(houseTableData);
        TitledPane tablePane = new TitledPane("LISTA LOCUINTE", tblHouses);
        tablePane.setCollapsible(false);
        VBox.setVgrow(tablePane, Priority.ALWAYS);

        // Search
        txtSearch = new TextField();
        txtSearch.setPrefWidth(200);
        txtSearch.setPromptText("Nume client...");
        Button btnSearch = new Button("Cauta");
        Button btnShowAll = new Button("Afiseaza Toate");
        btnSearch.setOnAction(e -> agencyPresenter.searchHouseByClient());
        btnShowAll.setOnAction(e -> agencyPresenter.housesList());

        HBox searchBox = new HBox(10, new Label("Nume Client:"), txtSearch, btnSearch, btnShowAll);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setPadding(new Insets(5));
        TitledPane searchPane = new TitledPane("CAUTARE LOCUINTE DUPA CLIENT", searchBox);
        searchPane.setCollapsible(false);

        //  Filter
        cmbFilterCriterion = new ComboBox<>(FXCollections.observableArrayList(
                "Locatie", "Pret (min-max)", "Tip locuinta", "Nr. camere"));
        cmbFilterCriterion.getSelectionModel().selectFirst();
        txtFilterValue = new TextField();
        txtFilterValue.setPrefWidth(160);
        txtFilterValue.setPromptText("Valoare filtru...");
        Button btnFilter = new Button("Filtreaza");
        btnFilter.setOnAction(e -> agencyPresenter.filterHouses());

        HBox filterBox = new HBox(10, new Label("Criteriu:"), cmbFilterCriterion,
                new Label("Valoare:"), txtFilterValue, btnFilter);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.setPadding(new Insets(5));
        TitledPane filterPane = new TitledPane("FILTRARE LOCUINTE", filterBox);
        filterPane.setCollapsible(false);

        root.getChildren().addAll(infoPane, imgPane, tablePane, searchPane, filterPane);
        return root;
    }

    //  TAB 2: CLIENTI
    private VBox buildClientsTab() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        txtClientID = new TextField();
        txtClientID.setEditable(false);
        txtClientID.setPrefWidth(80);
        txtClientName = new TextField();
        txtClientName.setPrefWidth(240);
        txtClientPhone = new TextField();
        txtClientPhone.setPrefWidth(160);
        txtClientEmail = new TextField();
        txtClientEmail.setPrefWidth(240);

        grid.add(new Label("ID:"), 0, 0);
        grid.add(txtClientID, 1, 0);
        grid.add(new Label("Nume:"), 0, 1);
        grid.add(txtClientName, 1, 1);
        grid.add(new Label("Telefon:"), 0, 2);
        grid.add(txtClientPhone, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(txtClientEmail, 1, 3);

        Button btnAdd = styledButton("Adauga", "#4CAF50");
        Button btnUpdate = styledButton("Actualizeaza", "#2196F3");
        Button btnDelete = styledButton("Sterge", "#F44336");

        btnAdd.setOnAction(e -> agencyPresenter.insertClient());
        btnUpdate.setOnAction(e -> agencyPresenter.updateClient());
        btnDelete.setOnAction(e -> agencyPresenter.deleteClient());

        HBox btnBox = new HBox(10, btnAdd, btnUpdate, btnDelete);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 2, 0, 2, 1);

        TitledPane infoPane = new TitledPane("INFORMATII CLIENT", grid);
        infoPane.setCollapsible(false);

        tblClients = buildTable(clientTableData);
        TitledPane tablePane = new TitledPane("LISTA CLIENTI", tblClients);
        tablePane.setCollapsible(false);
        VBox.setVgrow(tablePane, Priority.ALWAYS);

        root.getChildren().addAll(infoPane, tablePane);
        return root;
    }

    // TAB 3: CONTRACTE
    private VBox buildRentalsTab() {
        VBox root = new VBox(8);
        root.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10));

        txtRentalID = new TextField();
        txtRentalID.setEditable(false);
        txtRentalID.setPrefWidth(80);
        txtRentalHouseID = new TextField();
        txtRentalHouseID.setPrefWidth(120);
        txtRentalClientID = new TextField();
        txtRentalClientID.setPrefWidth(120);
        txtRentalStartDate = new TextField();
        txtRentalStartDate.setPrefWidth(140);
        txtRentalStartDate.setPromptText("yyyy-MM-dd");
        txtRentalEndDate = new TextField();
        txtRentalEndDate.setPrefWidth(140);
        txtRentalEndDate.setPromptText("yyyy-MM-dd (optional)");
        txtRentalMonthlyRent = new TextField();
        txtRentalMonthlyRent.setPrefWidth(120);

        grid.add(new Label("ID Contract:"), 0, 0);
        grid.add(txtRentalID, 1, 0);
        grid.add(new Label("ID Locuinta:"), 0, 1);
        grid.add(txtRentalHouseID, 1, 1);
        grid.add(new Label("ID Client:"), 0, 2);
        grid.add(txtRentalClientID, 1, 2);
        grid.add(new Label("Data Start:"), 2, 0);
        grid.add(txtRentalStartDate, 3, 0);
        grid.add(new Label("Data End:"), 2, 1);
        grid.add(txtRentalEndDate, 3, 1);
        grid.add(new Label("Chirie (EUR/luna):"), 2, 2);
        grid.add(txtRentalMonthlyRent, 3, 2);

        Button btnAdd = styledButton("Adauga", "#4CAF50");
        Button btnUpdate = styledButton("Actualizeaza", "#2196F3");
        Button btnDelete = styledButton("Sterge", "#F44336");

        btnAdd.setOnAction(e -> agencyPresenter.insertRental());
        btnUpdate.setOnAction(e -> agencyPresenter.updateRental());
        btnDelete.setOnAction(e -> agencyPresenter.deleteRental());

        HBox btnBox = new HBox(10, btnAdd, btnUpdate, btnDelete);
        btnBox.setAlignment(Pos.CENTER);
        grid.add(btnBox, 0, 3, 4, 1);

        Label hint = new Label("Nota: lasati Data End gol pentru contracte active. "
                + "La adaugare, locuinta va fi marcata automat ca indisponibila.");
        hint.setStyle("-fx-font-size: 11; -fx-text-fill: #666666; -fx-font-style: italic;");

        TitledPane infoPane = new TitledPane("INFORMATII CONTRACT DE INCHIRIERE", grid);
        infoPane.setCollapsible(false);

        tblRentals = buildTable(rentalTableData);
        TitledPane tablePane = new TitledPane("LISTA CONTRACTE", tblRentals);
        tablePane.setCollapsible(false);
        VBox.setVgrow(tablePane, Priority.ALWAYS);

        root.getChildren().addAll(infoPane, hint, tablePane);
        return root;
    }


    private void attachEventHandlers() {
        tblHouses.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal.intValue() >= 0) agencyPresenter.setHouseControls();
                });

        tblClients.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal.intValue() >= 0) agencyPresenter.setClientControls();
                });

        tblRentals.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal.intValue() >= 0) agencyPresenter.setRentalControls();
                });
    }


    private Button styledButton(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
        return btn;
    }

    private ImageView buildImageView() {
        ImageView iv = new ImageView();
        iv.setFitWidth(220);
        iv.setFitHeight(160);
        iv.setPreserveRatio(true);
        iv.setStyle("-fx-border-color: #aaaaaa; -fx-border-width: 1;");
        return iv;
    }

    private void loadImage(ImageView iv, String path) {
        if (path == null || path.trim().isEmpty()) {
            iv.setImage(null);
            return;
        }
        try {
            String uri = path.startsWith("http") ? path : new File(path).toURI().toString();
            Image img = new Image(uri, 220, 160, true, true, true);
            iv.setImage(img);
        } catch (Exception e) {
            iv.setImage(null);
        }
    }

    private <T extends Map<String, String>> TableView<T> buildTable(ObservableList<T> data) {
        TableView<T> table = new TableView<>(data);
        table.setPrefHeight(280);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private void populateTable(TableView<Map<String, String>> table,
                               ObservableList<Map<String, String>> tableData,
                               List<String[]> rows, String[] columns) {
        table.getColumns().clear();
        tableData.clear();

        for (String col : columns) {
            TableColumn<Map<String, String>, String> column = new TableColumn<>(col);
            column.setCellValueFactory(cellData -> {
                String value = cellData.getValue().get(col);
                return new javafx.beans.property.SimpleStringProperty(value != null ? value : "");
            });
            column.setMinWidth(70);
            table.getColumns().add(column);
        }

        for (String[] row : rows) {
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int i = 0; i < columns.length && i < row.length; i++) {
                rowMap.put(columns[i], row[i]);
            }
            tableData.add(rowMap);
        }
    }

    // IAgencyGUI - HOUSE FORM
    @Override
    public int getHouseID() {
        try {
            return Integer.parseInt(txtHouseID.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setHouseID(int id) {
        txtHouseID.setText(id > 0 ? String.valueOf(id) : "");
    }

    @Override
    public String getHouseAddress() {
        return txtAddress.getText();
    }

    @Override
    public void setHouseAddress(String a) {
        txtAddress.setText(a);
    }

    @Override
    public String getHouseLocation() {
        return txtLocation.getText();
    }

    @Override
    public void setHouseLocation(String l) {
        txtLocation.setText(l);
    }

    @Override
    public String getHouseTypeName() {
        return cmbHouseType.getValue();
    }

    @Override
    public void setHouseTypeName(String houseTypeName) {
        if (houseTypeName != null && !houseTypeName.trim().isEmpty()) {
            cmbHouseType.setValue(houseTypeName);
        } else {
            cmbHouseType.getSelectionModel().selectFirst();
        }
    }

    @Override
    public int getNumberOfRooms() {
        try {
            return Integer.parseInt(txtRooms.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setNumberOfRooms(int rooms) {
        txtRooms.setText(rooms > 0 ? String.valueOf(rooms) : "");
    }

    @Override
    public double getHousePrice() {
        try {
            return Double.parseDouble(txtPrice.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setHousePrice(double price) {
        txtPrice.setText(price > 0 ? String.valueOf(price) : "");
    }

    @Override
    public void setHouseImages(String img1, String img2, String img3) {
        loadImage(imgView1, img1);
        loadImage(imgView2, img2);
        loadImage(imgView3, img3);
    }

    //  IAgencyGUI - HOUSE TABLE
    @Override
    public void setHouseTable(List<String[]> rows, String[] columns) {
        currentHouseColumns = columns;
        populateTable(tblHouses, houseTableData, rows, columns);
    }

    @Override
    public int getSelectedHouseIndex() {
        return tblHouses.getSelectionModel().getSelectedIndex();
    }

    @Override
    public String getHouseIDFromTable(int index) {
        if (index >= 0 && index < houseTableData.size()) {
            return houseTableData.get(index).get(currentHouseColumns[0]);
        }
        return "0";
    }

    // IAgencyGUI - CLIENT
    @Override
    public int getClientID() {
        try {
            return Integer.parseInt(txtClientID.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setClientID(int id) {
        txtClientID.setText(id > 0 ? String.valueOf(id) : "");
    }

    @Override
    public String getClientName() {
        return txtClientName.getText();
    }

    @Override
    public void setClientName(String name) {
        txtClientName.setText(name);
    }

    @Override
    public String getClientPhone() {
        return txtClientPhone.getText();
    }

    @Override
    public void setClientPhone(String phone) {
        txtClientPhone.setText(phone);
    }

    @Override
    public String getClientEmail() {
        return txtClientEmail.getText();
    }

    @Override
    public void setClientEmail(String email) {
        txtClientEmail.setText(email);
    }

    @Override
    public void setClientTable(List<String[]> rows, String[] columns) {
        currentClientColumns = columns;
        populateTable(tblClients, clientTableData, rows, columns);
    }

    @Override
    public int getSelectedClientIndex() {
        return tblClients.getSelectionModel().getSelectedIndex();
    }

    @Override
    public String getClientIDFromTable(int index) {
        if (index >= 0 && index < clientTableData.size()) {
            return clientTableData.get(index).get(currentClientColumns[0]);
        }
        return "0";
    }

    // IAgencyGUI - RENTAL
    @Override
    public int getRentalID() {
        try {
            return Integer.parseInt(txtRentalID.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setRentalID(int id) {
        txtRentalID.setText(id > 0 ? String.valueOf(id) : "");
    }

    @Override
    public String getRentalHouseID() {
        return txtRentalHouseID.getText();
    }

    @Override
    public void setRentalHouseID(String id) {
        txtRentalHouseID.setText(id);
    }

    @Override
    public String getRentalClientID() {
        return txtRentalClientID.getText();
    }

    @Override
    public void setRentalClientID(String id) {
        txtRentalClientID.setText(id);
    }

    @Override
    public String getRentalStartDate() {
        return txtRentalStartDate.getText();
    }

    @Override
    public void setRentalStartDate(String date) {
        txtRentalStartDate.setText(date);
    }

    @Override
    public String getRentalEndDate() {
        return txtRentalEndDate.getText();
    }

    @Override
    public void setRentalEndDate(String date) {
        txtRentalEndDate.setText(date);
    }

    @Override
    public double getRentalMonthlyRent() {
        try {
            return Double.parseDouble(txtRentalMonthlyRent.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void setRentalMonthlyRent(double rent) {
        txtRentalMonthlyRent.setText(rent > 0 ? String.valueOf(rent) : "");
    }

    @Override
    public void setRentalTable(List<String[]> rows, String[] columns) {
        currentRentalColumns = columns;
        populateTable(tblRentals, rentalTableData, rows, columns);
    }

    @Override
    public int getSelectedRentalIndex() {
        return tblRentals.getSelectionModel().getSelectedIndex();
    }

    @Override
    public String getRentalIDFromTable(int index) {
        if (index >= 0 && index < rentalTableData.size()) {
            return rentalTableData.get(index).get(currentRentalColumns[0]);
        }
        return "0";
    }

    //  IAgencyGUI - SEARCH & FILTER
    @Override
    public String getSearchedInformation() {
        return txtSearch.getText();
    }

    @Override
    public void resetSearchedInformation() {
        txtSearch.setText("");
    }

    @Override
    public String getSelectedFilter() {
        return txtFilterValue.getText();
    }

    @Override
    public void resetSelectedFilter() {
        txtFilterValue.setText("");
    }

    @Override
    public int getSelectedFilterCriterion() {
        return cmbFilterCriterion.getSelectionModel().getSelectedIndex();
    }

    @Override
    public void resetSelectedFilterCriterion() {
        cmbFilterCriterion.getSelectionModel().selectFirst();
    }

    // IAgencyGUI - GENERAL
    @Override
    public void setVisibility(boolean visibility) {
        if (visibility) stage.show();
        else stage.hide();
    }

    @Override
    public void setMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}