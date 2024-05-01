package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private String text;

    public StringCalculator() {
    }

    public int add(String text) {

        // null or 빈 문자열 검증
        if(text == null || text.isEmpty()) {
            return 0;
        }

        // 음수 입력 시 예외 처리
        if(text.matches("^-\\d+$")){
            throw new RuntimeException();
        }

        String[] split;

        Matcher matcher = delimiter(text);

        if (matcher.find()) {
            String delimiter = matcher.group(1);
            split = matcher.group(2).split(delimiter);
        }else{
            split = text.split(",|:");
        }

        return Arrays.stream(split).mapToInt(Integer::parseInt).sum();
    }

    public Matcher delimiter(String text) {
        return Pattern.compile("//(.)\n(.*)").matcher(text);
    }

}
