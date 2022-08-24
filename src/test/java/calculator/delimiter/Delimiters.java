package calculator.delimiter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Delimiters {
    private final List<Delimiter> delimiters;

    public Delimiters(final List<Delimiter> delimiters) {
        if (Objects.isNull(delimiters) || delimiters.isEmpty()) {
            throw new RuntimeException("delimiter는 비어 있을 수 없습니다.");
        }
        this.delimiters = delimiters;
    }

    // TODO refactoring
    public List<String> split(final String expression) {
        List<String> splitExpressions = Arrays.asList(expression);
        for (Delimiter delimiter : delimiters) {
            splitExpressions = delimiter.split(splitExpressions);
        }
        return splitExpressions;
    }
}
