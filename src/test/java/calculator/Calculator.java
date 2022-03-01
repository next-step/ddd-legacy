package calculator;

import java.util.Arrays;

import static calculator.CalculatorUtil.isNullOrEmpty;
import static calculator.CalculatorUtil.toInt;
import static org.hibernate.query.criteria.internal.ValueHandlerFactory.isNumeric;

/* 구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다. */
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

        return Arrays.stream(numbers)
                .map(CalculatorUtil::toInt)
                .reduce(0, Integer::sum);
    }
}
