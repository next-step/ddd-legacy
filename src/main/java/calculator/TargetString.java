package calculator;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TargetString {
    private String target;
    private static final Pattern pattern = Pattern.compile("//(.)\\n");

    public TargetString(final String target) {
        if (target == null) {
            throw new IllegalArgumentException("값이 없습니다. 다시 입력해주세요");
        }
        this.target = target;
    }

    public Optional<Character> getDelimiterOrNull() {
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            target = target.replaceFirst(matcher.group(), "");
            return Optional.of(matcher.group(1).charAt(0));
        }
        return Optional.empty();
    }


    public char[] toCharArray() {
        return target.toCharArray();
    }
}
