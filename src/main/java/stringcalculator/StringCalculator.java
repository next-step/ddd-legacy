package stringcalculator;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StringCalculator {

    public int add(String text) {
        if (isEmpty(text)) {
            return 0;
        }
        List<ZeroOrPositiveNumber> numbers = convertToNumbers(text);
        return sum(numbers);
    }

    private boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }

    private List<ZeroOrPositiveNumber> convertToNumbers(String text) {
        String[] textArray = TextSplitter.split(text);

        return Arrays.stream(textArray)
                .map(ZeroOrPositiveNumber::new)
                .collect(toList());
    }

    private int sum(List<ZeroOrPositiveNumber> numbers) {
        ZeroOrPositiveNumbers numberList = new ZeroOrPositiveNumbers(numbers);
        return numberList.sum().getNumber();
    }
}
