package calculate;

public class StringCalculator {

  private static final String DEFAULT_DELIMITER = ",|:";

  public static int calculate(final String text) {
    if (text == null || text.isBlank()) {
      return 0;
    }

    String[] operands = text.split(DEFAULT_DELIMITER);
    int sum = 0;
    for (String operand : operands) {
      sum += Integer.parseInt(operand);
    }
    return sum;
  }

}
