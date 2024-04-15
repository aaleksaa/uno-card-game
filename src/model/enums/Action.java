package model.enums;

public enum Action {
    SKIP("Skip"),
    REVERSE("Reverse"),
    DRAW_TWO("Draw two");

    private final String action;

    private Action(String action) {
        this.action = action;
    }

    public static Action fromString(String action) {
        return switch (action) {
            case "Skip" -> SKIP;
            case "Reverse" -> REVERSE;
            case "Draw two" -> DRAW_TWO;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return action;
    }
}
