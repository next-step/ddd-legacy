package calculator;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringExtractor {

  private final static String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";
  private final static String DEFAULT_DELIMITER_REGEX = ",|:";

  public String[] extract(String text) {
    if (Objects.isNull(text) || text.isBlank()) {
      return new String[]{"0"};
    }
    Matcher m = Pattern.compile(CUSTOM_DELIMITER_PATTERN).matcher(text);
    if (m.find()) {
      String customDelimiter = m.group(1);
      return m.group(2).split(customDelimiter);
    }
    return text.split(DEFAULT_DELIMITER_REGEX);
  }
}
