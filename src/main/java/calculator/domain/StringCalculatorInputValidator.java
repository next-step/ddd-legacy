package calculator.domain;

import org.springframework.util.StringUtils;

import java.util.List;

import static calculator.domain.CalculatorException.*;

public class StringCalculatorInputValidator {

    private final StringCalculatorDelimiters delimiters;

    public StringCalculatorInputValidator(StringCalculatorDelimiters delimiters) {
        this.delimiters = delimiters;
    }

    public boolean isEmpty(final String input) {
        return !StringUtils.hasText(input);
    }

    public void assertPositiveNumbers(final List<Integer> numbers) {
        numbers.stream()
                .filter(n -> n < 0)
                .findAny()
                .ifPresent(n -> {
                    throw new InvalidInputException("음수는 입력할 수 없습니다.");
                });
    }

    public void assertValidInput(final String input) {
        if(isEmpty(input)) {
            return;
        }
        String processedInput = delimiters.extractAndAddCustomDelimiter(input);

        if (!isValidNumberList(processedInput)) {
            throw new InvalidInputException("잘못된 입력 형식입니다: " + input);
        }
    }

    private boolean isValidNumberList(String input) {
        for (String number : input.split(delimiters.getRegex())) {
            if (!isNumeric(number)) {
                return false;
            }
        }
        return true;
    }

    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
