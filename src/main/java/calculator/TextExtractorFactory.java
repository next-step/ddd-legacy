package calculator;

import java.util.ArrayList;
import java.util.List;

public class TextExtractorFactory {
  private final List<TextExtractor> textExtractors;

  public TextExtractorFactory(TextExtractor... textExtractors) {
    this.textExtractors = List.of(textExtractors);
  }

  public TextExtractor get(String text) {
    for (TextExtractor textExtractor : textExtractors) {
      if(textExtractor.isSupport(text)) {
        return textExtractor;
      }
    }
    throw new TextExtractionException("지원하는 본문 추출기가 존재하지 않습니다.");
  }
}
