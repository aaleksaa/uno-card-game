package model.enums;

public enum Color {
    RED("RED"),
    BLUE("BLUE"),
    GREEN("GREEN"),
    YELLOW("YELLOW"),
    WILD("WILD");

    private final String color;

    private Color(String color) {
        this.color = color;
    }

    public static Color fromString(String color) {
        return switch (color) {
            case "RED" -> RED;
            case "BLUE" -> BLUE;
            case "GREEN" -> GREEN;
            case "YELLOW" -> YELLOW;
            case "WILD" -> WILD;
            default -> null;
        };
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return color;
    }
}
