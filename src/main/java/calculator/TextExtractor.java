package calculator;

public interface TextExtractor {

  boolean isSupport(String text);

  String[] extract(String text);
}
