package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class CustomStringNumbers {
  private final List<CustomStringNumber> stringNumbers;

  public CustomStringNumbers(final String text) {
    final List<String> split = DelimiterSplit.split(text);
    this.stringNumbers = split.isEmpty() ? new ArrayList<>() : this.create(split);
  }

  public int sum() {
    return this.stringNumbers.isEmpty()
        ? 0
        : this.stringNumbers.stream().mapToInt(CustomStringNumber::getNumber).sum();
  }

  private List<CustomStringNumber> create(final List<String> split) {
    final List<CustomStringNumber> customStringNumbers = new ArrayList<>();

    for (String stringNumber : split) {
      customStringNumbers.add(new CustomStringNumber(stringNumber));
    }

    return customStringNumbers;
  }
}
