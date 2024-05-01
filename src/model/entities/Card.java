package model.entities;

import model.enums.Color;

public abstract class Card {
    protected Color color;

    public Card(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public abstract boolean match(String[] parts);
}
