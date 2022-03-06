package calculator.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StringNumbers {
    private final List<String> StringNumbers;

    public StringNumbers(List<String> stringNumbers) {
        this.StringNumbers = stringNumbers;
    }

    public List<Integer> parseInt() {
        List<Integer> intNumbers = new ArrayList<>();
        try {
            intNumbers = StringNumbers.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return intNumbers;
    }
}
