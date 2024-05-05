package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DelimiterSplit {

  public static List<String> split(final String text) {
    if (Objects.isNull(text) || text.isEmpty()) {
      return new ArrayList<>();
    }

    final String replaceAllText = text.replaceAll("[,:;]", ",");

    return Arrays.stream(replaceAllText.split(",")).map(String::strip).toList();
  }
}
