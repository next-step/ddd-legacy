package kitchenpos.stringcalculator;

public class ParsedNumber {
    private final int number;
    private static final int ZERO = 0;

    public ParsedNumber(String text) {
        this.number = Integer.parseInt(text);
        validate();
    }

    private void validate() {
        if (this.number < ZERO) {
            throw new IllegalArgumentException("음수 값");
        }
    }

    public int getNumber() {
        return number;
    }
}
