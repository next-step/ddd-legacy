package racingcar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    Integer result = 0;

    public Integer add(String input) {

        if (input == null || input.isEmpty()) {
            return 0;
        }

        Integer parseInt = isInteger(input);
        if (parseInt != null) {
            return parseInt;
        }

        String delimiter = ",:";
        String custom = findCustomDelimiter(input, "//(.*?)\\n");
        if (custom != null) {
            delimiter += custom;
            input = input.replaceAll("//(.*?)\\n", "");
        }

        String[] split = input.split("["+delimiter+"]");
        for (String s : split) {
            Integer num = isInteger(s);
            if (num != null) {
                result += num;
            }
        }

        return result;
    }

    // 숫자 변환
    Integer isInteger(String input) {
        try {
            Integer num = Integer.parseInt(input);
            if (num >= 0) {
                return Integer.parseInt(input);
            } else {        // 음수인 경우, RuntimeException 발생
                throw new RuntimeException("input number is negative : " + num);
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 커스텀 구분자 찾기
    String findCustomDelimiter(String input, String pattern) {
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

}
