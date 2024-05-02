package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private String text;

    private static StringValidation stringValidation = new StringValidation();
    private static DelimiterParser delimiterParser = new DelimiterParser();

    public StringCalculator(){
    }

    // add 메서드 내 각 검증을 메서드로 분리
    public int add(String text) {

        // 1. null & empty 검증 메서드
        if (stringValidation.isNullOrEmpty(text)) {
            return 0;
        }

        // 2. Dilimiter 파싱 및 숫자 추출 메서드
        String[] numbers = delimiterParser.parseDelimiter(text);

        // 3. 음수 검증 메서드
        stringValidation.checkNegative(numbers);

        // 4. 추출한 숫자의 총합 메서드
        return sumNumbers(numbers);
    }

    private int sumNumbers(String[] numbers) {
        return Arrays.stream(numbers).mapToInt(Integer::parseInt).sum();
    }


}
