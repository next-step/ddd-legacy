package stringcalculator;

public class StringCalculator {

  public int add(final String text) {
    final CustomStringNumbers customStringNumbers = new CustomStringNumbers(text);
    return customStringNumbers.sum();
  }
}
