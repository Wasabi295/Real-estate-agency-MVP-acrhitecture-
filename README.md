# Real-Estate Agency — MVP Architecture

JavaFX desktop application for a small rental agency, built around the **Model–View–Presenter (MVP)** architectural pattern. The view is completely passive: it does not know about the model and only exposes hooks the presenter can call. The presenter mediates between the JavaFX UI and the JDBC repositories.

A full design document is included as `Documentatie_MVP.pdf` for reference.

## Why MVP?

- **View** (`view/JavaFXGUI`) — only knows how to render and how to forward UI events to the presenter through an `IAgencyGUI` contract.
- **Presenter** (`presenter/AgencyPresenter`) — owns all UI logic: validation, formatting, listing, CRUD orchestration. It speaks to the model via repository interfaces.
- **Model** (`model/...`) — `House`, `Client`, `Rental` entities + JDBC repositories with `IHouseRepository`, `IClientRepository`, `IRentalRepository` interfaces, allowing the presenter to be tested with mocks.

The result is a UI that is easy to swap (e.g. for a CLI or a Swing front-end) without touching business or data-access code.

## Project layout

```
src/main/java/org/example/
├── Main.java                      # Wires DB connection + repositories + presenter + view
├── view/
│   └── JavaFXGUI.java             # Passive view (implements IAgencyGUI)
├── presenter/
│   ├── IAgencyGUI.java            # Contract the view must satisfy
│   └── AgencyPresenter.java       # All UI orchestration
└── model/
    ├── House.java, HouseType.java
    ├── Client.java
    ├── Rental.java
    └── repository/
        ├── DatabaseConnection.java
        ├── IHouseRepository.java   / HouseRepository.java
        ├── IClientRepository.java  / ClientRepository.java
        └── IRentalRepository.java  / RentalRepository.java
Documentatie_MVP.pdf                # Architecture write-up
```

## Requirements

- Java 21
- JavaFX 21
- MySQL 8 with a database named `RentalAgencyDB` (credentials are wired in `Main.java` — adjust them or override via your IDE)

## Run

```bash
./gradlew run
```

…or open the project in IntelliJ and run `org.example.Main`.

On startup the presenter loads the lists of houses, clients and rentals from the database and the JavaFX window appears with three populated tables. From there the user can add / edit / delete entities through the UI; every action goes through the presenter and the corresponding repository.
