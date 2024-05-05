package stringcalculator.calculator;

import stringcalculator.ParsedNumbers;

@FunctionalInterface
public interface Calculator {
     int execute(ParsedNumbers parsedNumbers);
}
