package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numbers {
  private final List<Number> numbers;

  private Numbers(List<Number> numbers) {
    this.numbers = new ArrayList<>(numbers);
  }

  public static Numbers create(NumberValidator numberValidator, String[] values) {
    List<Number> numbers = Arrays.stream(values)
        .map(value -> Number.create(numberValidator, value))
        .collect(Collectors.toList());
    return new Numbers(numbers);
  }

  public Number sum() {
    return numbers.stream()
        .reduce(Number.ZERO, Number::add);
  }
}
