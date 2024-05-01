package model.enums;

public enum WildAction {
    CHANGE_COLOR("CHANGE_COLOR"),
    DRAW_FOUR("DRAW_FOUR");

    private final String specialAction;

    private WildAction(String specialAction) {
        this.specialAction = specialAction;
    }

    public static WildAction fromString(String specialAction) {
        return switch (specialAction) {
            case "CHANGE_COLOR" -> CHANGE_COLOR;
            case "DRAW_FOUR" -> DRAW_FOUR;
            default -> null;
        };
    }

    public String getSpecialAction() {
        return specialAction;
    }

    @Override
    public String toString() {
        return specialAction;
    }
}
