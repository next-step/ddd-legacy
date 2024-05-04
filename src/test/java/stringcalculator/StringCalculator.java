package stringcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(final String text) {

        if (Objects.isNull(text) || text.isEmpty()) {
            return 0;
        }

        final Pattern compile = Pattern.compile("([-]?)([0-9])");
        final Matcher matcher = compile.matcher(text);

        final List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
//            final String findGroupValue = matcher.group();
//            if (findGroupValue.isEmpty()) {
//                continue;
//            }

            final int matcherNumber = Integer.parseInt(matcher.group());
            if (matcherNumber < 0) {
                throw new RuntimeException();
            }

            numbers.add(Integer.parseInt(matcher.group()));
        }

        return numbers.stream()
                .mapToInt(o -> o)
                .sum();
    }

}
