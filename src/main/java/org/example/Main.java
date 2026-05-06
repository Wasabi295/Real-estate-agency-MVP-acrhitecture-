package org.example;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.model.repository.*;
import org.example.presenter.AgencyPresenter;
import org.example.view.JavaFXGUI;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection dbConnection = new DatabaseConnection(
                "jdbc:mysql://localhost:3306/RentalAgencyDB", "root", "pass"
        );
        IHouseRepository houseRepo = new HouseRepository(dbConnection);
        IClientRepository clientRepo = new ClientRepository(dbConnection);
        IRentalRepository rentalRepo = new RentalRepository(dbConnection);

        JavaFXGUI gui = new JavaFXGUI(primaryStage);
        AgencyPresenter presenter = new AgencyPresenter(gui, houseRepo, clientRepo, rentalRepo);
        gui.setPresenter(presenter);

        presenter.housesList();
        presenter.clientsList();
        presenter.rentalsList();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}