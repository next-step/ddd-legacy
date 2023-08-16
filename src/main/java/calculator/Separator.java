package calculator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Separator {
    private final Set<Character> delimiters = new HashSet<>(List.of(':', ','));

    public NumberStrings separate(TargetString target) {
        NumberStrings numbers = new NumberStrings(new ArrayList<>());
        StringBuilder numberBuilder = new StringBuilder();
        target.getDelimiterOrNull().ifPresent(delimiters::add);
        searchNumbersFromTarget(target, numbers, numberBuilder);
        return numbers;
    }

    private void searchNumbersFromTarget(TargetString target, NumberStrings numberStrings, StringBuilder numberBuilder) {
        for (char ch : target.toCharArray()) {
            if (delimiters.contains(ch)) {
                numberStrings.addIfNotEmptyBuilder(numberBuilder);
                continue;
            }
            appendDigit(numberBuilder, ch);
        }

        numberStrings.addIfNotEmptyBuilder(numberBuilder);
    }

    private void appendDigit(StringBuilder numberBuilder, char ch) {
        if (!Character.isDigit(ch))
            throw new RuntimeException(ch + "는 허용된 구분자가 아니거나 숫자가 아닙니다.");

        numberBuilder.append(ch);
    }
}
