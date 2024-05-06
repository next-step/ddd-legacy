package stringcalculator.validator;

import stringcalculator.ParsedNumbers;
import stringcalculator.ValidatedNumbers;

@FunctionalInterface
public interface Validator {
    ValidatedNumbers execute(ParsedNumbers parsedNumbers);
}
