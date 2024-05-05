package stringcalculator;

import java.util.regex.Pattern;

public class CustomStringNumber {
  private static final Pattern STRING_NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
  private final Integer number;

  public CustomStringNumber(String stringNumber) {
    if (this.isNumber(stringNumber)) {
      this.number = Integer.parseInt(stringNumber);
      CustomStringNumberValidator.negativeNumberValid(this.number);

      return;
    }

    this.number = 0;
  }

  private boolean isNumber(final String stringNumber) {
    return STRING_NUMBER_PATTERN.matcher(stringNumber).matches();
  }

  public Integer getNumber() {
    return this.number;
  }
}
