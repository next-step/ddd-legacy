package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDelimiterTextExtractor implements TextExtractor {

  private final static String REGEX_PATTERN = "//(.)\n(.*)";
  private final static Pattern PATTERN = Pattern.compile(REGEX_PATTERN);

  @Override
  public boolean isSupport(String text) {
    return PATTERN.matcher(text)
        .find();
  }

  @Override
  public String[] extract(String text) {
    Matcher matcher = PATTERN.matcher(text);
    if (matcher.find()) {
      String customDelimiter = matcher.group(1);
      return matcher.group(2).split(customDelimiter);
    }
    throw new TextExtractionException("본문 추출에 실패했습니다.");
  }
}
