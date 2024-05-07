package calculator;

import java.util.regex.Pattern;

public class DefaultTextExtractor implements TextExtractor {

  private final static String REGEX_PATTERN = ",|:";
  private final Pattern pattern;

  public DefaultTextExtractor() {
    this.pattern = Pattern.compile(REGEX_PATTERN);
  }

  @Override
  public boolean isSupport(String text) {
    return pattern.matcher(text)
        .find();
  }

  @Override
  public String[] extract(String text) {
    if(!isSupport(text)) {
      throw new TextExtractionException("본문 추출에 실패했습니다.");
    }
    return text.split(REGEX_PATTERN);
  }
}
