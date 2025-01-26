package calculate.domain;

public class StringCalculate {
    private final Numbers numbers;

    public StringCalculate(final String sentence) {
        this(new Numbers(sentence));
    }

    public StringCalculate(final Numbers numbers) {
        this.numbers = numbers;
    }

    public int sum() {
        return numbers.sum();
    }

    public Numbers getNumbers() {
        return numbers;
    }

}
