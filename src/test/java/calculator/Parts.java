package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Parts {
    private final List<String> parts;

    public Parts(String... parts) {
        validate(parts);
        this.parts = List.of(parts);
    }

    public List<String> parts() {
        return parts;
    }

    public List<Integer> intParts() {
        return parts.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private void validate(String[] parts) {
        boolean isValidPattern = Arrays.stream(parts)
                .flatMapToInt(String::chars)
                .allMatch(Character::isDigit);
        if (!isValidPattern) {
            throw new RuntimeException("숫자 이외의 값은 허용하지 않습니다.");
        }
    }
}
