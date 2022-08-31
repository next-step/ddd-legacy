package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {

  private static final String COMMA_COLON = ",|:";
  private static final String CUSTOM_SEPARATOR_FOUND = "//(.)\n(.*)";
  private static final Pattern PATTERN_SEPARATOR_FOUND = Pattern.compile(CUSTOM_SEPARATOR_FOUND);

  private Matcher matcher;

  private final String text;

  public Separator(String text) {
    this.text = text;
  }

  public String[] numbers() {
    getMatcher();
    if (isMatcherFind()) {
      return getDelimiterNumbers().split(getCustomDelimiter() + "|" + COMMA_COLON);
    }
    return text.split(COMMA_COLON);
  }

  private void getMatcher() {
    matcher = PATTERN_SEPARATOR_FOUND.matcher(text);
  }

  private boolean isMatcherFind() {
    return matcher.find();
  }

  private String getCustomDelimiter() {
    return matcher.group(1);
  }

  private String getDelimiterNumbers() {
    return matcher.group(2);
  }
}
