package calculator;

import static calculator.CalculatorUtil.isNullOrEmpty;
import static calculator.CalculatorUtil.toInt;
import static org.hibernate.query.criteria.internal.ValueHandlerFactory.isNumeric;

/* 숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다. */
public class Calculator {

    public int add(String text) {
        if(isNullOrEmpty(text)) {
            return 0;
        }
        if(text.length()==1 && isNumeric(text)) {
            return toInt(text);
        }
        return 1;
    }
}
