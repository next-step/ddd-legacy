package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class CustomStringNumbers {
  private final List<CustomStringNumber> stringNumbers;

  public CustomStringNumbers(final String text) {
    this.stringNumbers = this.process(text);
  }

  public int sum() {
    return this.stringNumbers.stream()
            .mapToInt(CustomStringNumber::getNumber)
            .sum();
  }

  private List<CustomStringNumber> process(final String text) {
    final List<String> split = DelimiterSplit.split(text);

    if (split.isEmpty()) {
      return new ArrayList<>();
    }

    return this.create(split);
  }

  private List<CustomStringNumber> create(final List<String> split) {
    final List<CustomStringNumber> customStringNumbers = new ArrayList<>();

    for (String stringNumber : split) {
      customStringNumbers.add(new CustomStringNumber(stringNumber));
    }

    return customStringNumbers;
  }
}
