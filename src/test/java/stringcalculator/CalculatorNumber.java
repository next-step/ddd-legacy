package stringcalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class CalculatorNumber {
    private static final Map<Integer, CalculatorNumber> CACHE = new HashMap<>();
    private final int number;

    public static CalculatorNumber from(final String number) {
        int validatedNumber = validateNumber(number);
        return findCache(validatedNumber);
    }

    private CalculatorNumber(int number) {
        this.number = number;
    }

    private static CalculatorNumber findCache(final int validatedNumber) {
        return CACHE.computeIfAbsent(validatedNumber, CalculatorNumber::new);
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

    public static int sum(Stream<CalculatorNumber> numbers) {
        return numbers.mapToInt(CalculatorNumber::getNumber).sum();
    }

    public int getNumber() {
        return number;
    }

    public CalculatorNumber plus(CalculatorNumber input) {
        return new CalculatorNumber(this.number + input.number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculatorNumber that = (CalculatorNumber) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "CalculatorNumber{" +
                "number=" + number +
                '}';
    }
}
