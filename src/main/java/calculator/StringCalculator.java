package calculator;

import java.util.List;

public class StringCalculator {

    private NumberExtractor numberExtractor;

    public StringCalculator() {
        this.numberExtractor = new NumberExtractor();
    }

    public int add(String input) {
        if (input == null) return 0;
        if (input.isEmpty()) return 0;

        List<Integer> numbers = numberExtractor.extractNumbers(input);

        return numbers.stream()
                .reduce(Integer::sum)
                .orElseThrow(RuntimeException::new);
    }
}
