package model.enums;

public enum Action {
    SKIP("SKIP"),
    REVERSE("REVERSE"),
    DRAW_TWO("DRAW_TWO");

    private final String action;

    private Action(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public static Action fromString(String action) {
        return switch (action) {
            case "SKIP" -> SKIP;
            case "REVERSE" -> REVERSE;
            case "DRAW_TWO" -> DRAW_TWO;
            default -> null;
        };
    }

    @Override
    public String toString() {
        return action;
    }
}
