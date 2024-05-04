package stringcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class StringCalculator {

  public int add(final String text) {

    if (Objects.isNull(text) || text.isEmpty()) {
      return 0;
    }
    final String replaceAllText = text.replaceAll("[,:;]", ",");
    final String[] split = replaceAllText.split(",");
    final List<Integer> numbers = new ArrayList<>();

    for (String s : split) {
      final Pattern compile = Pattern.compile("-?\\d+(\\.\\d+)?");
      final String stripString = s.strip();

      if (compile.matcher(stripString).matches()) {
        final int number = Integer.parseInt(stripString);
        if (number < 0) {
          throw new RuntimeException();
        }

        numbers.add(number);
      }
    }

    return numbers.stream().mapToInt(o -> o).sum();
  }
}
