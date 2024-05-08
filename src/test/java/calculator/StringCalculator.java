package calculator;

import org.springframework.util.StringUtils;

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
    if (StringUtils.hasText(text)) {
      return ZERO;
    }

    List<Integer> numbers = extractor.extract(text);

    return numbers.stream()
            .reduce(0, Integer::sum);
  }
}
