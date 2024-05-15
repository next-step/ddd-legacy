package stringCalculator;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input {
    private final String[] inputStrings;

    public Input(String text) {
        this.validation(text);

        this.inputStrings = this.splitTextByRegex(text);
    }

    public String[] getInputStrings() {
        return inputStrings;
    }

    private void validation(String text){
        if(Optional.ofNullable(text).isEmpty() || text.isBlank()){
            throw new IllegalArgumentException("input text is empty");
        }
    }

    private String[] splitTextByRegex(String text) {
        final String defaultRegex = "[,\\:]";

        Pattern pattern = Pattern.compile("//(.)\\n(.*)");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String regex = matcher.group(1);
            String tempString = matcher.group(2).replaceAll("\n", "");

            return tempString.split(regex);
        }

        return text.split(defaultRegex);
    }
}
