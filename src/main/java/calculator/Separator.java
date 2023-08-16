package calculator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Separator {
    private final Set<Character> delimiters = new HashSet<>(List.of(':', ','));

    Pattern pattern = Pattern.compile("//(.)\\n");

    public List<String> separate(String target) {
        List<String> numbers = new ArrayList<>();
        StringBuilder numberBuilder = new StringBuilder();
        target = replaceIfMatchPattern(target);
        searchNumbersFromTarget(target, numbers, numberBuilder);
        return numbers;
    }

    private String replaceIfMatchPattern(String target) {
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            delimiters.add(matcher.group(1).charAt(0));
            target = target.replaceFirst(matcher.group(), "");
        }
        return target;
    }

    private void searchNumbersFromTarget(String target, List<String> nums, StringBuilder numberBuilder) {
        for (char ch : target.toCharArray()) {
            boolean separateCondition = delimiters.contains(ch) && numberBuilder.length() > 0;
            if (separateCondition) {
                nums.add(getNumber(numberBuilder));
                continue;
            }

            appendDigit(numberBuilder, ch);
        }

        if (numberBuilder.length() > 0)
            nums.add(getNumber(numberBuilder));
    }

    private void appendDigit(StringBuilder numberBuilder, char ch) {
        if (!Character.isDigit(ch))
            throw new RuntimeException(ch + "는 허용된 구분자가 아니거나 숫자가 아닙니다.");

        numberBuilder.append(ch);
    }

    private String getNumber(StringBuilder numberBuilder) {
        String number = numberBuilder.toString();
        numberBuilder.setLength(0);
        return number;
    }
}
