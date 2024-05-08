package calculator;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

class NumberExtractor {
  private static final String DELIMITER_PREFIX = "//";
  private static final String DELIMITER_POSTFIX = "\n";

  private static final Pattern DEFAULT_DELIMITER = Pattern.compile("[,:]");
  private static final Pattern DELIMITER_PREFIX_REGEX = Pattern.compile("[//]");
  private static final Pattern DELIMITER_POSTFIX_REGEX = Pattern.compile("[\n].*");

  // TODO 이 부분 에서 if를 없애는 방향으로 객체지향을 사용할 수 있을 것 같습니다. 좋은 아이디어가 있으시면 공유 부탁드릴께요!
  public List<Integer> extract(String text) {

    validateEmpty(text);

    if (startsWithCustomDelimiter(text)) {
      String delimiter = extractDelimiter(text);

      String[] numbers = text
              .replaceAll(DELIMITER_PREFIX, "")
              .replaceAll(DELIMITER_POSTFIX, "")
              .split(delimiter);

      return Arrays.stream(numbers)
              .filter(StringUtils::hasText)
              .map(this::parseInt)
              .toList();
    }

    String[] numbers = DEFAULT_DELIMITER.split(text);

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
      throw new IllegalArgumentException("text is empty: Please check nullity of text");
  }

  private Integer parseInt(final String text) {
    try {
      Integer value = Integer.valueOf(text);

      if (value.compareTo(0) < 0)
        throw new RuntimeException("숫자가 음수 입니다.");

      return value;

    } catch (NumberFormatException e) {

      throw new RuntimeException("숫자가 아닌 값이 text에 있습니다.");
    }
  }
}
