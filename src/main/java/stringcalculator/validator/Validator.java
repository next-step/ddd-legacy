package stringcalculator.validator;

import stringcalculator.ParsedNumbers;

@FunctionalInterface
public interface Validator {
    void execute(ParsedNumbers parsedNumbers);
}
