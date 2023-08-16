package calculator;

import java.util.List;

public class NumberStrings {
    private final List<String> numbers;

    public NumberStrings(List<String> numbers) {
        this.numbers = numbers;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public boolean isEmpty() {
        return numbers.isEmpty();
    }

    public void add(StringBuilder numberBuilder) {
        String number = numberBuilder.toString();
        numberBuilder.setLength(0);
        numbers.add(number);
    }

    public void addIfNotEmptyBuilder(StringBuilder numberBuilder) {
        if (numberBuilder.length() > 0) {
            this.add(numberBuilder);
        }
    }

}