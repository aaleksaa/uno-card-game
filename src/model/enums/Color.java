package model.enums;

/**
 * The Color enum represents the different colors of cards in the Uno card game.
 * Each color has a corresponding string representation.
 */
public enum Color {
    RED("RED"),
    BLUE("BLUE"),
    GREEN("GREEN"),
    YELLOW("YELLOW"),
    WILD("WILD");

    private final String color;

    /**
     * Constructs a Color with the specified color string.
     *
     * @param color the string representation of the color
     */
    private Color(String color) {
        this.color = color;
    }

    /**
     * Returns the Color corresponding to the specified string.
     * If the string does not match any color, it returns null.
     *
     * @param color the string representation of the color
     * @return the Color corresponding to the specified string, or null if no match is found
     */
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

    /**
     * Returns the string representation of the color.
     *
     * @return the string representation of the color
     */
    @Override
    public String toString() {
        return color;
    }
}
