package stringcalculator;

import java.util.List;
import java.util.Objects;

public class StringCalculator {


  public int add(final String text) {
    if (Objects.isNull(text) || text.isEmpty()) {
      return 0;
    }

    final TransformNumber transformNumber = new TransformNumber();
    final List<Integer> numbers = transformNumber.numbers(text);

    return numbers.stream().mapToInt(o -> o).sum();
  }

}
