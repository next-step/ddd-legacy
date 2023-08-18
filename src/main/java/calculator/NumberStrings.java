package calculator;

import java.util.ArrayList;
import java.util.List;

public class NumberStrings {
    private final List<String> numbers;

    public NumberStrings(List<String> numbers) {
        if (numbers == null) {
            numbers = new ArrayList<>();
        }
        this.numbers = numbers;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public boolean isEmpty() {
        return numbers.isEmpty();
    }

    public void addIfNotEmpty(StringBuilder stringBuilder) {
        if (stringBuilder.length() <= 0) {
            return;
        }
        String number = stringBuilder.toString();
        stringBuilder.setLength(0);
        numbers.add(number);
    }

}
