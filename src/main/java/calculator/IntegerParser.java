package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class IntegerParser {
    private List<Integer> textToIntArray;

    public IntegerParser(String[] text) {
        textToIntArray = textToIntArray(text);
    }

    private List<Integer> getIntArray() {
        return textToIntArray;
    }

    private List<Integer> textToIntArray(String[] text) {
        return Arrays.stream(text).map(this::convertTextToInt).collect(Collectors.toList());
    }

    public List<Number> convertNumberArray() {
        return this.getIntArray().stream()
                .map(Number::new)
                .collect(Collectors.toList());
    }

    private int convertTextToInt(String textNumber) {
        try {
            return (Integer.parseInt(textNumber));
        } catch (Exception e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
    }
}
