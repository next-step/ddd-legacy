package calculator;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class NumberExtractor {
  private static final String DELIMITER_PREFIX = "//";
  private static final String DELIMITER_POSTFIX = "\n";

  private static final Pattern DEFAULT_DELIMITER = Pattern.compile("[,:]");
  private static final Pattern DELIMITER_PREFIX_REGEX = Pattern.compile("[//]");
  private static final Pattern DELIMITER_POSTFIX_REGEX = Pattern.compile("[\n].*");

  public List<PositiveNumber> extract(String text) {

    validateEmpty(text);

    if (startsWithCustomDelimiter(text)) {
      String delimiter = extractDelimiter(text);

      String[] numbers = extractNumbers(text, delimiter);

      return extractIntegers(numbers);
    }

    String[] numbers = DEFAULT_DELIMITER.split(text);

    return extractIntegers(numbers);
  }

  @NotNull
  private static String[] extractNumbers(String text, String delimiter) {
    String[] numbers = text
            .replaceAll(DELIMITER_PREFIX, "")
            .replaceFirst(delimiter, "")
            .replaceAll(DELIMITER_POSTFIX, "")
            .split(delimiter);
    return numbers;
  }

  @NotNull
  private List<PositiveNumber> extractIntegers(String[] numbers) {
    return Arrays.stream(numbers)
            .map(this::parseInt)
            .toList();
  }

  private boolean startsWithCustomDelimiter(final String text) {
    return text.startsWith(DELIMITER_PREFIX);
  }

  private String extractDelimiter(final String text) {
    String removePrefix = DELIMITER_PREFIX_REGEX.matcher(text).replaceAll("");
    String delimiter = DELIMITER_POSTFIX_REGEX.matcher(removePrefix).replaceAll("");

    return delimiter;
  }

  private void validateEmpty(final String text) {
    if (ObjectUtils.isEmpty(text))
      throw new IllegalArgumentException("변환 대상 인풋이 빈값이거나 null 입니다.");
  }

  private PositiveNumber parseInt(final String element) {
    try {

      return PositiveNumber.from(element);

    } catch (NumberFormatException e) {

      throw new RuntimeException(String.format("연산 대상은 숫자이어야 합니다. 입력된 글자: %s", element));
    }
  }
}
