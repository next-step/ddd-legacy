package calculator;

import java.util.regex.Pattern;

public class DefaultTextExtractor implements TextExtractor {

  private final static String REGEX_PATTERN = ",|:";
  @Override
  public boolean isSupport(String text) {
    return true;
  }

  @Override
  public String[] extract(String text) {
    return text.split(REGEX_PATTERN);
  }
}
