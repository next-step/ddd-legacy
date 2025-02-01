package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Separator {

    private final List<String> separators = new ArrayList<>(Arrays.asList(",", ":"));

    public Stream<NotNegativeNumber> separate(String input) {
        Pattern pattern = Pattern.compile("//(.*?)\\n");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String customSeparator = matcher.group(1);
            separators.add(customSeparator);

            int matchedLength = matcher.end();
            String remains = input.substring(matchedLength);
            return extractNumbers(remains);
        }

        return extractNumbers(input);
    }


    private Stream<NotNegativeNumber> extractNumbers(String input) {
        String regex = "[" + String.join("", separators) + "]";
        String[] split = input.split(regex);
        return Arrays.stream(split)
                .map(NotNegativeNumber::new);
    }
}
