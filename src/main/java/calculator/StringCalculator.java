package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

  public int add(String text) {
    if(text == null || text.isBlank()) {
      return 0;
    }
    String[] tokens;
    Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
    if (m.find()) {
      String customDelimiter = m.group(1);
      tokens = m.group(2).split(customDelimiter);
      List<Integer> numbers = NumberConverter.convert(tokens);
      for (Integer value : numbers) {
        if(value < 0) {
          throw new RuntimeException();
        }
      }
      return Arrays.stream(tokens)
          .mapToInt(Integer::parseInt)
          .sum();
    }
    tokens = text.split("[,:]");
    List<Integer> numbers = NumberConverter.convert(tokens);
    for (Integer value : numbers) {
      if(value < 0) {
        throw new RuntimeException();
      }
    }
    return Arrays.stream(tokens)
        .mapToInt(Integer::parseInt)
        .sum();
  }
}
