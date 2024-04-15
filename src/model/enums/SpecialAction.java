package model.enums;

public enum SpecialAction {
    CHANGE_COLOR("Change color"),
    DRAW_FOUR("Draw four");

    private final String specialAction;

    private SpecialAction(String specialAction) {
        this.specialAction = specialAction;
    }

    public static SpecialAction fromString(String specialAction) {
        return switch (specialAction) {
            case "Change color" -> CHANGE_COLOR;
            case "Draw four" -> DRAW_FOUR;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return specialAction;
    }
}
