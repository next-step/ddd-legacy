package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

  public int add(String text) {
    if(text == null || text.isBlank()) {
      return 0;
    }
    String[] tokens;
    Integer[] values;
    Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
    if (m.find()) {
      String customDelimiter = m.group(1);
      tokens = m.group(2).split(customDelimiter);
      values = Arrays.stream(tokens)
          .map(Integer::parseInt)
          .toArray(Integer[]::new);
      for (Integer value : values) {
        if(value < 0) {
          throw new RuntimeException();
        }
      }
      return Arrays.stream(tokens)
          .mapToInt(Integer::parseInt)
          .sum();
    }
    tokens = text.split("[,:]");
    values = Arrays.stream(tokens)
        .map(Integer::parseInt)
        .toArray(Integer[]::new);
    for (Integer value : values) {
      if(value < 0) {
        throw new RuntimeException();
      }
    }
    return Arrays.stream(tokens)
        .mapToInt(Integer::parseInt)
        .sum();
  }
}
