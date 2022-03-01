package calculator;

import java.util.Arrays;

import static calculator.CalculatorUtil.isNullOrEmpty;
import static calculator.CalculatorUtil.toInt;
import static org.hibernate.query.criteria.internal.ValueHandlerFactory.isNumeric;

/* 숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다. */
public class Calculator {

    public int add(String text) {
        int answer = 0;

        if(isNullOrEmpty(text)) {
            return answer;
        }
        if(text.length()==1 && isNumeric(text)) {
            return toInt(text);
        }
        String[] numbers = text.split(",");

        return Arrays.stream(numbers)
                .map(CalculatorUtil::toInt)
                .reduce(0, Integer::sum);
    }
}
