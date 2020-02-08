package calculator;

import org.apache.logging.log4j.util.Strings;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static Pattern CUSTOM_DELIMTER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (Strings.isEmpty(text)) { return 0;}

        Matcher matcher = CUSTOM_DELIMTER_PATTERN.matcher(text);
        if (matcher.find()) {
            String[] split = getPatternStringArray(matcher);
            return getTextArraySum(split);
        }
        return getTextArraySum(toSplitArrayList(text));
    }

    private String[] getPatternStringArray(Matcher matcher) {
        String delimiter = matcher.group(1);
        return matcher.group(2).split(delimiter);
    }

    private Integer getTextArraySum(String[] text) {
        return Arrays.stream(text)
                .map(this::convertTextToInt)
                .reduce(0, Integer::sum);
    }

    private String[] toSplitArrayList(String text) {
        return text.split(",|:");
    }

    private int convertTextToInt(String text) {
        try {
            return getPostiveNumber(Integer.parseInt(text));
        } catch (Exception e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
    }

    private int getPostiveNumber(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("음수 입력 에러");
        }
        return number;
    }
}
