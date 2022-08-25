package addstring;

import java.util.Arrays;

public class Number {

    public int convertStringNumbersToIntSum(String[] array) {
        validateNumber(array);
        return Arrays.stream(array)
            .mapToInt(Integer::parseInt)
            .sum();
    }

    private void validateNumber(String[] array) {
        boolean hasNegativeNumber = Arrays.stream(array)
            .mapToInt(Integer::parseInt)
            .anyMatch(s -> s < 0);

        if (hasNegativeNumber) {
            throw new IllegalArgumentException("음수를 입력할 수 없습니다.");
        }
    }
}
