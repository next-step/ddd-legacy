package stringcalculator;

import java.util.List;
import java.util.regex.Pattern;

public class TransformNumber {

  public List<Integer> numbers(String text) {
    final DelimiterSplit delimiterSplit = new DelimiterSplit("[,:;]");
    final List<String> stringList = delimiterSplit.getStrings(text);
    return this.transform(stringList);
  }

  private List<Integer> transform(List<String> stringList) {
    return stringList.stream().map(this::find).toList();
  }

  private Integer find(String s) {
    final int number = this.matcher(s) ? Integer.parseInt(s) : 0;
    CalculatorValidator.negativeNumberValid(number);

    return number;
  }

  private boolean matcher(String s) {
    final Pattern compile = Pattern.compile("-?\\d+(\\.\\d+)?");
    return compile.matcher(s).matches();
  }
}
