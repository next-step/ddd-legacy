package calculate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

  private static final String DEFAULT_DELIMITER = ",|:";
  private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

  public static int calculate(final String text) {
    if (text == null || text.isBlank()) {
      return 0;
    }

    String[] operands = split(text);
    int sum = 0;
    for (String operand : operands) {
      sum += new PositiveNumber(operand).getNumber();
    }
    return sum;
  }

  private static String[] split(String text) {
    Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
    if (matcher.find()) {
      String customDelimiter = matcher.group(1);
      return matcher.group(2).split(customDelimiter);
    }
    return text.split(DEFAULT_DELIMITER);
  }

}
