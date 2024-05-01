package model.entities;

import model.enums.Color;
import model.enums.WildAction;

public class WildCard extends Card {
    private WildAction wildAction;

    public WildCard(WildAction specialAction) {
        super(Color.WILD);
        this.wildAction = specialAction;
    }

    public WildAction getWildAction() {
        return wildAction;
    }

    @Override
    public String toString() {
        return color + " - " + wildAction;
    }

    @Override
    public boolean match(String[] parts) {
        Color inputColor = Color.fromString(parts[0]);
        WildAction inputWildAction = WildAction.fromString(parts[1]);

        return color.getColor().equals(parts[0]) && wildAction.getSpecialAction().equals(parts[1]);
    }
}
