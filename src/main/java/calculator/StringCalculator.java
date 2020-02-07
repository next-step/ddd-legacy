package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private NumberExtractor numberExtractor;
    private final String REGEX = "//(.)\n(.*)";

    public int add(String input) {
        if (input == null) return 0;
        if (input.isEmpty()) return 0;

        selectNumberExtractor(input);

        List<Integer> numbers = numberExtractor.extractNumbers(input);

        return numbers.stream()
                .reduce(Integer::sum)
                .orElseThrow(RuntimeException::new);
    }

    protected void selectNumberExtractor(String input) {

        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(input);

        this.numberExtractor = matcher.find() ? new CustomNumberExtractor() : new DefaultNumberExtractor();
    }

    public NumberExtractor getNumberExtractor() {

        return numberExtractor;
    }
}
