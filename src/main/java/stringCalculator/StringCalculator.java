package stringCalculator;

import io.micrometer.core.instrument.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private String text;

    public int add(String text) {

        int parseInt = 0;

        if (StringUtils.isBlank(text)) {
            return parseInt;
        }

        // 참고. https://enterkey.tistory.com/353
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            int sum = 0;
            String customDelimiter = m.group(1);                    // (.)의 값
            String[] tokens = m.group(2).split(customDelimiter);    // (.*)의 값
            for (String tmpStr : tokens) {
                sum += Integer.parseInt(tmpStr);
            }
            return sum;
        }

        if (text.indexOf(",") > -1 || text.indexOf(":") > -1) {
            int sum = 0;
            String[] tokens = text.split(",|:");
            for (String tmpStr : tokens) {
                sum += Integer.parseInt(tmpStr);
            }
            return sum;
        }

        try {
            parseInt = Integer.parseInt(text);
            if (parseInt < 0) {
                throw new RuntimeException("음수를 사용할 수 없습니다.");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("숫자 이외의 값을 사용할 수 없습니다.");
        }

        return parseInt;
    }


}
