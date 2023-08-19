package stringaddcalculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private static final String regex = "[,|:]";
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;

    private int result;

    public int add(String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            return ZERO;
        }

        if (text.length() == ONE) {
            return Integer.parseInt(text);
        }

        Matcher m = pattern.matcher(text);
        if (m.find()) {
            addByUniquePattern(m);
        }

        String[] tokens = text.split(regex);
        return parseAdd(tokens);
    }

    private void addByUniquePattern(Matcher m) {
        String customDelimiter = m.group(ONE);
        String [] tokens = m.group(TWO).split(customDelimiter);
        parseAdd(tokens);
    }

    private int parseAdd(String [] tokens) {
        for (String value : tokens) {
            int number = Integer.parseInt(value);
            validateMinus(number);
            result += number;
        }
        return result;
    }

    private void validateMinus(int number) {
        if (number < ZERO) {
            throw new RuntimeException("음수 값은 사용할 수 없는 값입니다.");
        }
    }
}
