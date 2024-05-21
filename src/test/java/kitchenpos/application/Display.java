package kitchenpos.application;

public enum Display {
    DISPLAYED(true),
    HIDDEN(false);

    private final boolean value;
    Display(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
