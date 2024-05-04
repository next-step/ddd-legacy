package stringcalculator;

import java.util.Arrays;
import java.util.List;

public class DelimiterSplit {
  private final String regex;

  public DelimiterSplit(String regex) {
    this.regex = regex;
  }

  public List<String> getStrings(String text) {
    final String replaceAllText = text.replaceAll(this.regex, ",");

    return Arrays.stream(replaceAllText.split(","))
            .map(String::strip)
            .toList();
  }
}
