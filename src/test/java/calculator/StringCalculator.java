package calculator;

import java.util.Arrays;

public class StringCalculator {

  public int add(String text) {
    if (text == null || text.isBlank()) {
      return 0;
    }

    String[] result = text.split(",");

    return Arrays.stream(result).mapToInt(Integer::parseInt).sum();
  }
}
