package calculator;

public class StringCalculator {

  private final NumberValidator numberValidator;
  private final TextExtractorFactory textExtractorFactory;

  public StringCalculator() {
    this.numberValidator = new PositiveNumberValidator();
    this.textExtractorFactory = new TextExtractorFactory(
        new EmptyTextExtractor(),
        new CustomDelimiterTextExtractor(),
        new DefaultTextExtractor()
    );
  }

  public Number add(String text) {
    TextExtractor textExtractor = textExtractorFactory.get(text);
    String[] tokens = textExtractor.extract(text);
    Numbers numbers = Numbers.create(numberValidator, tokens);
    return numbers.sum();
  }
}
