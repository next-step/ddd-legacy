package stringcalculator;

import java.util.Objects;

public class CustomStringNumber {
  private final Integer number;

  public CustomStringNumber(String stringNumber) {
    CustomStringNumberValidator.nullValid(stringNumber);
    CustomStringNumberValidator.sizeValid(stringNumber);
    this.number = this.conversion(stringNumber);
  }

  private Integer conversion(String stringNumber) {
    if (DelimiterSplit.isNumber(stringNumber)) {
      final int number = Integer.parseInt(stringNumber);
      CustomStringNumberValidator.negativeNumberValid(number);

      return number;
    }

    return 0;
  }

  public Integer getNumber() {
    return this.number;
  }
}
