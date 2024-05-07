package calculator;

public class StringCalculator {

  private final NumberValidator numberValidator;
  private final StringExtractor stringExtractor;

  public StringCalculator() {
    this.numberValidator = new PositiveNumberValidator();
    this.stringExtractor = new StringExtractor();
  }

  public Number add(String text) {
    String[] tokens = stringExtractor.extract(text);
    Numbers numbers = Numbers.create(numberValidator, tokens);
    return numbers.sum();
  }
}
