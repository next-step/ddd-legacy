package calculator;

import java.util.Arrays;
import java.util.Objects;

public class StringCalculator {

  private static final int TEXT_NULL_OR_EMPTY_VALUE = 0;

  public int add(String text) {
    if (isTextBlank(text)) {
      return TEXT_NULL_OR_EMPTY_VALUE;
    }

    String[] result = getNumbers(text);

    return Arrays.stream(result)
        .map(PositiveInteger::getInstance)
        .reduce(PositiveInteger.ZERO_POSITIVE, PositiveInteger::add)
        .getNum();
  }

  private boolean isTextBlank(String text) {
    return Objects.isNull(text) || text.isBlank();
  }

  private String[] getNumbers(String text) {
    Separator separator = new Separator(text);
    return separator.numbers();
  }
}
