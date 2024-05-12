package stringCalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class Calculator {

    final String word;
    final String[] delimiters;
    final CustomDelimiterCondition customCondition;

    public Calculator(String word, String[] delimiters, CustomDelimiterCondition customCondition){
       this.word = word;
       this.delimiters = delimiters;
       this.customCondition = customCondition;
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

    public int getSum(){
        String customDelimiter = findCustomDelimiter(this.word, this.customCondition.getStart(), this.customCondition.getEnd());

        List<String> delimiterConditions = mergeCondition(customDelimiter);

        String[] words = splitWord(this.word, delimiterConditions);

        return Arrays.stream(words)
                .mapToInt(this::getValidNumber)
                .sum();
    }

    private List<String> mergeCondition(String customDelimiter) {
        List<String> delimiters = new ArrayList<String>();
        delimiters.add(customDelimiter);
        delimiters.addAll(Arrays.asList(this.delimiters));
        return delimiters;
    }


    protected String[] splitWord(String word, List<String> conditions) {
        word = detachCustomDelimiter(word);

        for (String condition : conditions) {
            word = word.replace(condition, ",");
        }

        return word.split(",");
    }

    private String detachCustomDelimiter(String word) {
        String target  = this.customCondition.getEnd().replace("\\", "\\\\");
        String[] results = word.split(target);

        if(results.length == 2){
            return results[1];
        }
        return results[0];
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
