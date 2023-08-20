package stringaddcalculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");
    private static final String regex = "[,|:]";
    private static final int ZERO = 0;
    private static final int ONE_LENGTH = 1;
    private static final int CUSTOM_DELIMITER_CHARACTER = 1;
    private static final int STRING_WITH_CUSTOM_DELIMITER = 2;

    private int result;

    public int add(String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            return ZERO;
        }

        Matcher m = pattern.matcher(text);
        if (m.find()) {
            return addByUniquePattern(m);
        }

        String[] tokens = text.split(regex);
        if (tokens.length == ONE_LENGTH) {
            return parseOneValue(tokens);
        }
        return parseAdd(tokens);
    }

    private int addByUniquePattern(Matcher m) {
        String customDelimiter = m.group(CUSTOM_DELIMITER_CHARACTER);
        String [] tokens = m.group(STRING_WITH_CUSTOM_DELIMITER).split(customDelimiter);
        return parseAdd(tokens);
    }

    private int parseOneValue(String[] token) {
        int value = Integer.parseInt(token[ZERO]);
        validateMinus(value);
        return value;
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
