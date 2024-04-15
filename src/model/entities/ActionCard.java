package model.entities;

import model.enums.Action;
import model.enums.Color;

public class ActionCard extends Card {
    private final Action action;

    public ActionCard(Color color, Action action) {
        super(color);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "[" + color + " - " + action + "]";
    }
}
