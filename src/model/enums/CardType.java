package model.enums;

public enum CardType {
    NUMBER("NUMBER"),
    SKIP("SKIP"),
    REVERSE("REVERSE"),
    DRAW_TWO("DRAW_TWO"),
    CHANGE_COLOR("CHANGE_COLOR"),
    DRAW_FOUR("DRAW_FOUR");

    private final String type;

    private CardType(String type) {
        this.type = type;
    }

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

    @Override
    public String toString() {
        return type;
    }
}
