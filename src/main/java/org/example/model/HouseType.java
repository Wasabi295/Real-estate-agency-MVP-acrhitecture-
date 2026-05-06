package org.example.model;

public enum HouseType {
    Apartment,
    Studio,
    House,
    Villa,
    Penthouse;


    public static HouseType fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Apartment; // fallback
        }

        String normalized = name.trim().toLowerCase();

        switch (normalized) {
            case "apartament":
                return Apartment;
            case "garsoniera":
                return Studio;
            case "casa":
                return House;
            case "vila":
                return Villa;
            case "penthouse":
                return Penthouse;
            default:
                return Apartment;
        }
    }


    public String toRomanianName() {
        switch (this) {
            case Apartment:
                return "Apartament";
            case Studio:
                return "Garsoniera";
            case House:
                return "Casa";
            case Villa:
                return "Vila";
            case Penthouse:
                return "Penthouse";
            default:
                return this.name();
        }
    }


    public static HouseType fromInt(int value) {
        if (value >= 0 && value < values().length) {
            return values()[value];
        }
        return Apartment;
    }

    public static String[] getNames() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) {
            names[i] = values()[i].name();
        }
        return names;
    }
}