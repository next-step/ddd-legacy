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

  public static Numbers create(String[] values) {
    List<Number> numbers = Arrays.stream(values)
        .map(Number::createPositive)
        .collect(Collectors.toList());
    return new Numbers(numbers);
  }

  public Number sum() {
    return numbers.stream()
        .reduce(Number.ZERO, Number::add);
  }
}
