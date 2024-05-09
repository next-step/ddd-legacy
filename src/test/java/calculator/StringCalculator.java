package calculator;

import org.springframework.util.ObjectUtils;

import java.util.List;

class StringCalculator {

  private static final StringCalculator calculator = new StringCalculator();
  private static final NumberExtractor extractor = new NumberExtractor();

  public static final int ZERO = 0;

  private StringCalculator() {
  }

  public static StringCalculator getInstance() {
    return calculator;
  }

  public int add(final String text) {
    if (ObjectUtils.isEmpty(text)) {
      return ZERO;
    }

    List<PositiveNumber> numbers = extractor.extract(text);

    return numbers.stream()
            .map(PositiveNumber::getValue)
            .reduce(0, Integer::sum);
  }
}
