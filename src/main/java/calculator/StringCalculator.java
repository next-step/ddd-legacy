package calculator;

import java.util.List;

public class StringCalculator {

    private NumberExtractor numberExtractor;

    public int add(String input) {
        if (input == null) return 0;
        if (input.isEmpty()) return 0;

        getNumberExtractor(input);

        List<Integer> numbers = numberExtractor.extractNumbers();

        return numbers.stream()
                .reduce(Integer::sum)
                .orElseThrow(RuntimeException::new);
    }

    protected void getNumberExtractor(String input) {

        this.numberExtractor = new NumberExtractor(input);
    }
}
