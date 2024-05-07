package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
  private final NumberValidator numberValidator;

  public StringCalculator() {
    this.numberValidator = new PositiveNumberValidator();
  }

  public Number add(String text) {
    if(text == null || text.isBlank()) {
      return Number.ZERO;
    }
    String[] tokens;
    Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
    if (m.find()) {
      String customDelimiter = m.group(1);
      tokens = m.group(2).split(customDelimiter);
      Numbers numbers = Numbers.create(numberValidator, tokens);
      return numbers.sum();
    }
    tokens = text.split("[,:]");
    Numbers numbers = Numbers.create(numberValidator, tokens);
    return numbers.sum();
  }
}
