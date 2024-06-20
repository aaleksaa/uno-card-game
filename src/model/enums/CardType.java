package model.enums;

/**
 * The CardType enum represents the different types of cards in the Uno card game.
 * Each type has a corresponding string representation.
 */
public enum CardType {
    NUMBER("NUMBER"),
    SKIP("SKIP"),
    REVERSE("REVERSE"),
    DRAW_TWO("DRAW_TWO"),
    CHANGE_COLOR("CHANGE_COLOR"),
    DRAW_FOUR("DRAW_FOUR");

    private final String type;

    /**
     * Constructs a CardType with the specified type string.
     *
     * @param type the string representation of the card type
     */
    private CardType(String type) {
        this.type = type;
    }

    /**
     * Returns the CardType corresponding to the specified string.
     * If the string does not match any card type, it returns null.
     *
     * @param type the string representation of the card type
     * @return the CardType corresponding to the specified string, or null if no match is found
     */
    public static CardType fromString(String type) {
        return switch (type) {
            case "NUMBER" -> NUMBER;
            case "SKIP" -> SKIP;
            case "REVERSE" -> REVERSE;
            case "DRAW_TWO" -> DRAW_TWO;
            case "CHANGE_COLOR" -> CHANGE_COLOR;
            case "DRAW_FOUR" -> DRAW_FOUR;
            default -> null;
        };
    }

    /**
     * Returns the string representation of the card type.
     *
     * @return the string representation of the card type
     */
    @Override
    public String toString() {
        return type;
    }
}
