package model.enums;

public enum WildAction {
    CHANGE_COLOR("Change color"),
    DRAW_FOUR("Draw four");

    private final String specialAction;

    private WildAction(String specialAction) {
        this.specialAction = specialAction;
    }

    public static WildAction fromString(String specialAction) {
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
