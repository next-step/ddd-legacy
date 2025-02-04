package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public StringCalculator() {
    }

    public int add(String text) {
        if (text == null || text.length() == 0) {
            return 0;
        }

        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);
        String separator = ",|:"; // 기본 구분자

        // 구분자 사이에 값이 있으면 separator 을 구분자로 변경
        if (matcher.matches()) {
            separator = Pattern.quote(matcher.group(1));
            text = matcher.group(2);
        }

        String[] split = split(text, separator);
        int init = 0;

        try {
            for (String s : split) {
                int i = Integer.parseInt(s);
                if (i < 0) {
                    throw new RuntimeException("문자열 계산기에 음수를 전달할 수 없다.");
                }
                init += i;
            }
            return init;
        } catch (Exception e) {
            throw new RuntimeException("문자열 계산기에 숫자 이외의 값을 전달 할 수 없다.");
        }
    }

    private String[] split(String text, String separator) {
        return text.split(separator);
    }
}
