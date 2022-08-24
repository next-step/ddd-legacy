package calculator.delimiter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@FunctionalInterface
public interface Delimiter {

    List<String> split(List<String> expressions);

    default List<String> split(List<String> expressions, String delimiter) {
        return expressions.stream()
                          .map(expression -> expression.split(delimiter))
                          .flatMap(Arrays::stream)
                          .collect(Collectors.toList());
    }
}
