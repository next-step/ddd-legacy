package calculator;

public class StringCalculator {

  private final TextExtractorFactory textExtractorFactory;

  public StringCalculator() {
    this.textExtractorFactory = new TextExtractorFactory(
        new EmptyTextExtractor(),
        new CustomDelimiterTextExtractor(),
        new DefaultTextExtractor()
    );
  }

  public Number add(String text) {
    TextExtractor textExtractor = textExtractorFactory.get(text);
    String[] tokens = textExtractor.extract(text);
    Numbers numbers = Numbers.create(tokens);
    return numbers.sum();
  }
}
