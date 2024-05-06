package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private final String[] parsedNum;

    public StringCalculator(String text){
        StringValidation stringValidation = new StringValidation(text);
        String parseReulst = stringValidation.validateNum();
        this.parsedNum = new DelimiterParser().parseDelimiter(parseReulst);
    }

    public int add() {
       return Arrays.stream(parsedNum).mapToInt(Integer::parseInt).sum();
    }



}
