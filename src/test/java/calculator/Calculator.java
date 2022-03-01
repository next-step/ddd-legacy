package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.CalculatorUtil.isNullOrEmpty;
import static calculator.CalculatorUtil.toInt;
import static org.hibernate.query.criteria.internal.ValueHandlerFactory.isNumeric;

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
                .map(CalculatorUtil::toInt)
                .reduce(0, Integer::sum);
    }
}
