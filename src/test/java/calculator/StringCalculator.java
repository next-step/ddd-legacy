package calculator;

import java.util.Arrays;

public class StringCalculator {

  public int add(String text) {
    if (isTextBlank(text)) {
      return 0;
    }

    String[] result = getNumbers(text);
    return Arrays.stream(result)
        .mapToInt(this::toInteger)
        .sum();
  }

  private boolean isTextBlank(String text) {
    return text == null || text.isBlank();
  }

  private String[] getNumbers(String text) {
    Separator separator = new Separator(text);
    return separator.numbers();
  }

  private int toInteger(String token) {
    int result = Integer.parseInt(token);
    validateNegativeInteger(result);
    return result;
  }

  private void validateNegativeInteger(int num) {
    if (num < 0) {
      throw new RuntimeException("음수는 계산할 수 없습니다.");
    }
  }
}
