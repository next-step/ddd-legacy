package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {

  private static final String COMMA_COLON = ",|:";
  private static final String CUSTOM_SEPARATOR_FOUND = "//(.)\n(.*)";

  private final String text;
  private final Matcher matcher;

  public Separator(String text) {
    this.text = text;
    this.matcher = Pattern.compile(CUSTOM_SEPARATOR_FOUND).matcher(text);
  }

  public String[] numbers() {
    return isMatcherFind() ?
        getDelimiterNumbers().split(getCustomDelimiter() + "|" + COMMA_COLON) :
        text.split(COMMA_COLON);
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
