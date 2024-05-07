package stringcalculator;

import java.util.Objects;

public class CustomStringNumberValidator {

  public static void negativeNumberValid(final int number) {
    if (number < 0) {
      throw new RuntimeException();
    }
  }

  public static void nullValid(final String stringNumber) {
    if (Objects.isNull(stringNumber))
      throw new RuntimeException("값을 입력해주세요.");
  }

  public static void sizeValid(final String stringNumber) {
    if (stringNumber.length() > 10)
      throw new RuntimeException("10자리 미만이어야 합니다.");
  }
}
