package calculator.model;

public class PositiveNumber {
    int number;

    public PositiveNumber(int number) {
        this.number = number;
        this.validate(number);
    }

    private void validate(int number) {
        if (number < 0) {
            throw new RuntimeException("Number must be positive");
        }
    }

    public int getNumber() {
        return this.number;
    }


}
