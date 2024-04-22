package model.entities;

import model.enums.Color;
import model.enums.WildAction;

public class WildCard extends Card {
    private WildAction specialAction;

    public WildCard(WildAction specialAction) {
        super(Color.WILD);
        this.specialAction = specialAction;
    }

    public WildAction getSpecialAction() {
        return specialAction;
    }

    @Override
    public String toString() {
        return color + " - " + specialAction;
    }
}
