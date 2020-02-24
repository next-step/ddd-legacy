package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class NumberParser {
    private List<Integer> numberArray;

    public NumberParser(String[] text) {
        numberArray = textToNumber(text);
    }

    public List<Integer> getNumberArray() {
        return numberArray;
    }

    public List<Integer> textToNumber(String[] text) {
        return Arrays.stream(text).map(this::convertTextToInt).collect(Collectors.toList());
    }
    private int convertTextToInt(String textNumber) {
        try {
            return (Integer.parseInt(textNumber));
        } catch (Exception e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
    }
}
