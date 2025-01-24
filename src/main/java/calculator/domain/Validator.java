package calculator.domain;

import org.springframework.util.StringUtils;

import java.util.List;

public class Validator {

    public boolean isEmpty(String input) {
        return StringUtils.isEmpty(input);
    }

    public void assertNoNegativeNumbers(List<Integer> numbers) {
        numbers.stream()
                .filter(n -> n < 0)
                .findAny()
                .ifPresent(n -> {
                    throw new RuntimeException("음수는 입력할 수 없습니다.");
                });
    }
}
