package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class DelimiterSplit {

  private static final Pattern STRING_NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

  public static List<String> split(final String text) {
    if (Objects.isNull(text) || text.isEmpty()) {
      return new ArrayList<>();
    }

    final String replaceAllText = text.replaceAll("[,:;]", ",");

    return Arrays.stream(replaceAllText.split(",")).map(String::strip).toList();
  }

  /***
   * <h1>숫자 확인</h1>
   * <pre>
   *     isNumber("1")   = true
   *     isNumber("123") = true
   *     isNumber("-1")  = true
   *     isNumber("A")   = false
   *     isNumber("ABC") = false
   * </pre>
   */
  public static boolean isNumber(final String stringNumber) {
    return STRING_NUMBER_PATTERN.matcher(stringNumber).matches();
  }
}
