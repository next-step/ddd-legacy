package stringCalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class Calculator {

    final String word;
    final String[] delimiters;
    final CustomDelimiterCondition condition;

    public Calculator(String word, String[] delimiters, CustomDelimiterCondition condition){
       this.word = word;
       this.delimiters = delimiters;
       this.condition = condition;
    }

    public int getNumber(){
        String customDelimiter = findCustomDelimiter(this.word, this.condition.getStart(), this.condition.getEnd());
        List<String> delimiters = new ArrayList<String>();

        delimiters.add(customDelimiter);
        delimiters.addAll(Arrays.asList(this.delimiters));

        String[] words = splitWord(this.word, delimiters);
        return Arrays.stream(words)
                .mapToInt(this::getValidNumber)
                .sum();
    }

    protected String findCustomDelimiter(String word, String startChar, String endChar) {
        startChar = Pattern.quote(startChar);
        endChar = Pattern.quote(endChar);

        String patternString = startChar + "(.+)" + endChar;
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(word);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return ",";
    }

    protected String[] splitWord(String word, List<String> conditions) {
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
