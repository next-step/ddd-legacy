package stringcalculator.validator;

import java.util.List;
import stringcalculator.ParsedNumbers;

public class NonNegativeIntegerValidator implements Validator {
    public static final NonNegativeIntegerValidator instance = new NonNegativeIntegerValidator();

    public static NonNegativeIntegerValidator getInstance() {
        return instance;
    }

    private NonNegativeIntegerValidator() {
    }

    @Override
    public void execute(ParsedNumbers parsedNumbers) {
        List<Integer> result = parsedNumbers.getIntegers();
        if (result.stream().anyMatch(it -> it < 0)) {
            throw new IllegalArgumentException("음수는 처리하지 않습니다");
        }
    }
}
