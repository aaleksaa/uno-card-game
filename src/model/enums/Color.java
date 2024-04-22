package model.enums;

public enum Color {
    RED("Red"),
    BLUE("Blue"),
    GREEN("Green"),
    YELLOW("Yellow"),
    WILD("Wild");

    private final String color;

    private Color(String color) {
        this.color = color;
    }

    public static Color fromString(String color) {
        return switch (color) {
            case "Red" -> RED;
            case "Blue" -> BLUE;
            case "Green" -> GREEN;
            case "Yellow" -> YELLOW;
            case "Wild" -> WILD;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return color;
    }
}
