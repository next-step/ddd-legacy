package stringcalculator;

import java.util.regex.Pattern;

public class CustomStringNumber {
  private static final Pattern STRING_NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
  private final Integer number;

  public CustomStringNumber(String stringNumber) {
    this.number = this.conversion(stringNumber);
  }

  private Integer conversion(String stringNumber) {
    if (this.isNumber(stringNumber)) {
      final int number = Integer.parseInt(stringNumber);
      CustomStringNumberValidator.negativeNumberValid(number);

      return number;
    }

    return 0;
  }

  /***
   * <h1>숫자 확인</h1>
   * <pre>
   *     isNumber("1")   = true
   *     isNumber("123") = true
   *     isNumber("-1")  = true
   *     isNumber("A")   = false
   *     isNumber("ABC") = false
   * </pre>
   */
  private boolean isNumber(final String stringNumber) {
    return STRING_NUMBER_PATTERN.matcher(stringNumber).matches();
  }

  public Integer getNumber() {
    return this.number;
  }
}
