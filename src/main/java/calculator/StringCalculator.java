package calculator;

public record StringCalculator(
    InputValidator inputValidator,
    DelimiterParser delimiterParser,
    NumberParser numberParser,
    SumCalculator sumCalculator
) {

  public int add(String input) {
    inputValidator.validate(input);

    String[] tokens = delimiterParser.parse(input);

    int[] numbers = numberParser.parse(tokens);

    return sumCalculator.calculate(numbers);
  }
}