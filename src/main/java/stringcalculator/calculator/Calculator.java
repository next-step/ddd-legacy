package stringcalculator.calculator;

import stringcalculator.ValidatedNumbers;

@FunctionalInterface
public interface Calculator {
     int execute(ValidatedNumbers parsedNumbers);
}
