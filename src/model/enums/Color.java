package model.enums;

public enum Color {
    RED("Red"),
    BLUE("Blue"),
    GREEN("Green"),
    YELLOW("Yellow"),
    SPECIAL("Special");

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
            case "Special" -> SPECIAL;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return color;
    }
}
