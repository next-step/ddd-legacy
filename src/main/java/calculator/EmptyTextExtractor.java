package calculator;

import java.util.Objects;

public class EmptyTextExtractor implements TextExtractor {
  private final static String DEFAULT_EXTRACT_VALUE = "0";
  @Override
  public boolean isSupport(String text) {
    return Objects.isNull(text) || text.isBlank();
  }

  @Override
  public String[] extract(String text) {
    if(!isSupport(text)) {
      throw new TextExtractionException("본문 추출에 실패했습니다.");
    }
    return new String[]{DEFAULT_EXTRACT_VALUE};
  }
}
