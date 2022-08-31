package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {

  private static final String COMMA_COLON = ",|:";
  private static final String CUSTOM_SEPARATOR_FOUND = "//(.)\n(.*)";
  private final Pattern PATTERN_SEPARATOR_FOUND = Pattern.compile(CUSTOM_SEPARATOR_FOUND);

  private final String text;

  public Separator(String text) {
    this.text = text;
  }

  public String[] numbers() {
    if (isMatcherFind()) {
      return getDelimiterNumbers().split(getCustomDelimiter() + "|" + COMMA_COLON);
    }
    return text.split(COMMA_COLON);
  }

  private boolean isMatcherFind() {
    return getMatcher().find();
  }

  private String getCustomDelimiter() {
    return getMatcher().group(1);
  }

  private String getDelimiterNumbers() {
    return getMatcher().group(2);
  }

  private Matcher getMatcher() {
    return PATTERN_SEPARATOR_FOUND.matcher(text);
  }
}
