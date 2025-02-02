package calculator;

public record NotNegativeNumber(Integer value) {

    public NotNegativeNumber(String value) {
        this(Integer.parseInt(value));
    }

    public NotNegativeNumber {
        if (value < 0) {
            throw new NegativeNotAllowedException();
        }
    }

    public NotNegativeNumber add(NotNegativeNumber number) {
        return new NotNegativeNumber(value + number.value);
    }
}
