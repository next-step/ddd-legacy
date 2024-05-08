package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

    private final String[] parsedNum;
    private int addResult;

    public StringCalculator(String text){
        StringValidation stringValidation = new StringValidation(text);
        String parseReulst = stringValidation.getText();
        this.parsedNum = new DelimiterParser(parseReulst).getParsedNumber();
    }

    public void add() {
        addResult = Arrays.stream(parsedNum).mapToInt(Integer::parseInt).sum();
    }

    public int showResult() {
        return addResult;
    }
}
