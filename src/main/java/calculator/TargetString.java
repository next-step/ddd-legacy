package calculator;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TargetString {
    String target;

    final Pattern pattern = Pattern.compile("//(.)\\n");

    public TargetString(String target) {
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
