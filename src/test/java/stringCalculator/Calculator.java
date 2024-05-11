package stringCalculator;

import java.util.Arrays;

public class Calculator {

    final String word;
    final String[] conditions;

    public Calculator(String word, String[] conditions){
       this.word = word;
       this.conditions = conditions;
    }

    public int getNumber(){
        String[] words = splitWord(this.word, this.conditions);
        return Arrays.stream(words)
                .mapToInt(this::getValidNumber)
                .sum();
    }

    protected String[] splitWord(String word, String[] conditions) {
        for (String condition : conditions) {
            word = word.replace(condition, ",");
        }
        return word.split(",");
    }

    protected int getValidNumber(String targetChar) {
        if (!isNumber(targetChar)) {
            throw new RuntimeException("invalid character, it should be number and over 0, given: " + targetChar);
        }

        return Integer.parseInt(targetChar);
    }

    private boolean isNumber(String singleChar) {
        if (singleChar == null || singleChar.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(singleChar);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
