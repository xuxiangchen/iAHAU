package net.peacesky.iahau.fragment;

public class DrawerItem {

    private final Type type;
    private String text;

    public DrawerItem(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public DrawerItem setText(String text) {
        this.text = text;
        return this;
    }

    public enum Type {HEADER, MENU, DIVIDER}
}