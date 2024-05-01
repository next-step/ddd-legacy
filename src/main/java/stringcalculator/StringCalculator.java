package stringcalculator;

public class StringCalculator {
    private final Numbers numbers;

    public StringCalculator(String userInput) {
        this.numbers = new Numbers(userInput);
    }

    public int calculate() {
        return numbers.sum();
    }
}
