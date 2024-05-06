package stringcalculator.validator;

import java.util.List;
import stringcalculator.ParsedNumbers;
import stringcalculator.ValidatedNumbers;

public class NonNegativeIntegerValidator implements Validator {
    public static final NonNegativeIntegerValidator instance = new NonNegativeIntegerValidator();

    public static NonNegativeIntegerValidator getInstance() {
        return instance;
    }

    private NonNegativeIntegerValidator() {
    }

    @Override
    public ValidatedNumbers execute(ParsedNumbers parsedNumbers) {
        List<Integer> result = parsedNumbers.getIntegers();
        if (result.stream().anyMatch(it -> it == null || it < 0)) {
            throw new IllegalArgumentException("null 과 음수는 처리하지 않습니다");
        }
        return ValidatedNumbers.of(parsedNumbers.getIntegers());
    }
}
