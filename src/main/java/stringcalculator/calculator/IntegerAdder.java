package stringcalculator.calculator;

import stringcalculator.ValidatedNumbers;

public class IntegerAdder implements Calculator {
    private static final IntegerAdder instance = new IntegerAdder();
    public static IntegerAdder getInstance() {
        return instance;
    }
    private IntegerAdder() {
    }

    @Override
    public int execute(ValidatedNumbers parsedNumbers) {
        return parsedNumbers.getIntegers().stream().reduce(0, Integer::sum);
    }
}
