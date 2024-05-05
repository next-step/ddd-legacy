package stringcalculator;

public class CustomStringNumberValidator {

  public static void negativeNumberValid(final int number) {
    if (number < 0) {
      throw new RuntimeException();
    }
  }
}
