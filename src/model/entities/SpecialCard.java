package model.entities;

import model.enums.Color;
import model.enums.SpecialAction;

public class SpecialCard extends Card {
    private SpecialAction specialAction;

    public SpecialCard(SpecialAction specialAction) {
        super(Color.SPECIAL);
        this.specialAction = specialAction;
    }

    public SpecialAction getSpecialAction() {
        return specialAction;
    }

    @Override
    public String toString() {
        return "[" + color + " - " + specialAction + "]";
    }
}
