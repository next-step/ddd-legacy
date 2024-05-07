package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private final String[] parsedNum;

    public StringCalculator(String text){
        StringValidation stringValidation = new StringValidation(text);
        String parseReulst = stringValidation.getText();
        this.parsedNum = new DelimiterParser(parseReulst).getParsedNumber();
    }

    public int add() {
       return Arrays.stream(parsedNum).mapToInt(Integer::parseInt).sum();
    }



}
