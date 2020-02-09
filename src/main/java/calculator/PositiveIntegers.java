package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class PositiveIntegers {
    private List<Integer> list;

    public PositiveIntegers(final String[] tokens) {
        final List<Integer> unsafeList = initializeList(tokens);
        if (!verifyHasNegativeNumber(unsafeList)) {
            throw new RuntimeException("입력값으로 음수가 사용됨");
        }
        setVerifiedList(unsafeList);
    }

    private void setVerifiedList(final List<Integer> list) {
        this.list = list;
    }

    private List<Integer> initializeList(final String[] tokens) {
        return Arrays.stream(tokens)
                .map(token -> (token.isEmpty() ? 0 : Integer.parseInt(token)))
                .collect(Collectors.toList());
    }

    private boolean verifyHasNegativeNumber(final List<Integer> list) {
        boolean hasNegative = list.stream().anyMatch(item -> item < 0);
        return !hasNegative;
    }

    public int operate(BinaryOperator<Integer> operator) {
        return list.stream()
                .reduce(0, operator);
    }
}
