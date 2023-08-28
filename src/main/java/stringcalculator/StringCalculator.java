package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

  public Integer add(String str) {
    if (!isValid(str)) {
      return 0;
    }

    if (1 == str.length()) {
      return Integer.parseInt(str);
    }

    return sumNumbers(parseNumbers(str));
  }

  private boolean isValid(String str) {
    return str != null && !str.isEmpty();
  }

  private String[] parseNumbers(String str) {
    Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(str);
    if (hasCustomDelimiter(matcher)) {
      String customDelimiter = matcher.group(1);

      return matcher.group(2).split(customDelimiter);
    }

    return str.split("[,:]");
  }

  private boolean hasCustomDelimiter(Matcher matcher) {
    return matcher.find();
  }

  private int convertNumberToInt(String str) {
    int num = Integer.parseInt(str);
    if (num < 0) {
      throw new IllegalArgumentException("계산기에 음수가 전달되었습니다.");
    }

    return num;
  }

  public Integer sumNumbers(String[] numbers) {
    return Arrays.stream(numbers)
        .mapToInt(this::convertNumberToInt)
        .sum();
  }
}
