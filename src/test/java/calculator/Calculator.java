package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.CalculatorUtil.isNullOrEmpty;
import static calculator.CalculatorUtil.toInt;

/* //와 \n 문자 사이에 커스텀 구분자를 지정할 수 있다. */
public class Calculator {

    public int add(String text) {
        int answer = 0;

        if(isNullOrEmpty(text)) {
            return answer;
        }
        if(text.length()==1 && isNumeric(text)) {
            return toInt(text);
        }
        String[] numbers = text.split(",|:");
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);

        if(matcher.find()) {
            String delimiter = matcher.group(1);
            numbers = matcher.group(2).split(delimiter);
        }

        return Arrays.stream(numbers)
                .filter(this::isNumeric)
                .map(CalculatorUtil::toInt)
                .filter(this::isPositive)
                .reduce(0, Integer::sum);
    }

    private boolean isPositive(Integer number) {
        if(number < 0) {
            throw new RuntimeException("문자열 계산기에 음수는 입력될 수 없습니다.");
        }
        return true;
    }

    private boolean isNumeric(String text) {
        try {
            Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
