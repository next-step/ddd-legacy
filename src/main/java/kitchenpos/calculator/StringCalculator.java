package kitchenpos.calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO
 * 3. 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculator {
    private static final String DEFAULT_DELIMITERS = "[,:]";
    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";

    public int sum(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] strings = split(text);
        int[] ints = toInts(strings);
        return sum(ints);
    }

    private String[] split(String text) {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITERS);
    }

    private int[] toInts(String[] strings) {
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    private int sum(int[] ints) {
        int sum = 0;
        for (int i : ints) {
            sum += i;
        }
        return sum;
    }
}
