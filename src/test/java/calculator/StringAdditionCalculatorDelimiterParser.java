package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringAdditionCalculatorDelimiterParser {
    private final List<Character> delimiters;
    private final String numbers;

    StringAdditionCalculatorDelimiterParser(String text) {
        ArrayList<Character> delimiters = new ArrayList(List.of(',', ';'));
        String numbers = text;
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            delimiters.add(m.group(1).charAt(0));
            numbers = m.group(2);
        }
        this.numbers = numbers;
        this.delimiters = delimiters;
    }

    public String getDelimiters() {
        return delimiters.stream().map(String::valueOf).collect(Collectors.joining());
    }

    public String getNumbers() {
        return numbers;
    }
}
