package calculator.vo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Integers {
    private final List<Integer> integers;

    public Integers(final String[] values) {
        List<Integer> integers = integerTypeValidate(values);
        negativeValidate(integers);
        this.integers = integers;
    }

    private List<Integer> integerTypeValidate(String[] values) {

        List<Integer> integers;
        try {
            integers = Arrays.stream(values)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("숫자가 아닌 문자는 입력할 수 없습니다.");
        }

        return integers;
    }

    private static void negativeValidate(List<Integer> integers) {
        boolean result = integers.stream()
                .anyMatch(integer -> integer < 0);

        if (result) throw new RuntimeException("음수는 입력할 수 없습니다.");
    }

    public List<Integer> getIntegers() {
        return integers;
    }
}
