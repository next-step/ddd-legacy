package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NumberConverter {
  public static List<Integer> convert(String[] number) {
    return Arrays.stream(number)
        .map(Integer::parseInt)
        .collect(Collectors.toList());
  }
}
