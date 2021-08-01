package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {

    public static final int DEFAULT_VALUE = 0;

    public int add(final String text) {
        if (text == null || text.isEmpty()) {
            return DEFAULT_VALUE;
        }

        String[] textArr = this.parsingDelimiter(text);
        List<Number> numbers = Stream.of(textArr)
                                    .map(Number::new)
                                    .collect(Collectors.toList());

        return this.calculate(numbers);
    }

    private String[] parsingDelimiter(final String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);

        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split("[,:]");
    }

    private int calculate(final List<Number> numbers) {
        int sum = 0;
        for (Number number : numbers) {
            sum += number.getValue();
        }
        return sum;
    }
}
