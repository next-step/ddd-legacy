package calculator.source;

import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
    private final List<Number> numbers;

    private Numbers(final List<String> numberList) {
        numbers = numberList.stream()
                .map(Number::new).collect(Collectors.toList());
    }

    public static Numbers from(final List<String> numberList) {
        return new Numbers(numberList);
    }

    public Number plusAll() {
        return numbers.stream()
                .reduce(Number::plus)
                .orElseThrow(() -> new RuntimeException("덧셈을 실패했습니다."));
    }
}
