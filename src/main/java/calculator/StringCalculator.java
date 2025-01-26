package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(final String text) {
        if (text == null || text.isEmpty()) return 0;
        return validate(text);
    }

    private int validate(String number) {
        try {
            int num = Integer.parseInt(number);
            if (num < 0) {
                throw new RuntimeException("양의 정수를 입력하세요.");
            }
            return num;
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자를 입력하세요.");
        }
    }

}
