package stringcalculator;

import java.util.HashMap;
import java.util.Map;

public class CalculatorNumber {
    private static Map<Integer, CalculatorNumber> cache = new HashMap();
    private final int number;

    public static CalculatorNumber from(final String number) {
        int validatedNumber = validateNumber(number);
        return findCache(validatedNumber);
    }

    private CalculatorNumber(int number) {
        this.number = number;
    }

    private static CalculatorNumber findCache(final int validatedNumber) {
        CalculatorNumber calculatorNumber = cache.get(validatedNumber);
        if (calculatorNumber == null) {
            CalculatorNumber number = new CalculatorNumber(validatedNumber);
            cache.put(validatedNumber, number);
            return number;
        }
        return calculatorNumber;
    }

    public static int validateNumber(String number) {
        try {
            int generatedNumber = Integer.parseInt(number);
            if (generatedNumber < 0) {
                throw new IllegalArgumentException("음수값은 처리할 수 없습니다.");
            }

            return generatedNumber;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("올바른 숫자 입력 값이 아닙니다.");
        }
    }

    public int getNumber() {
        return number;
    }
}
