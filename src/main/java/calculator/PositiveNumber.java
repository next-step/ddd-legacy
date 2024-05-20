package calculator;


import java.util.regex.Pattern;

public class PositiveNumber {
    private static final String NUMERIC_PATTERN = "[0-9]+";
    private static final Pattern NUMERIC = Pattern.compile(NUMERIC_PATTERN);
    private int inputNumber;

    public PositiveNumber(String inputNumber){
        validationNumber(inputNumber);
        this.inputNumber = Integer.parseInt(inputNumber);
    }

    public int getNumber(){
        return this.inputNumber;
    }

    private void validationNumber(String inputNumber){
        if(!isNumeric(inputNumber)){
            throw new RuntimeException("숫자가 아닙니다.");
        }
        if(!isPositive(inputNumber)){
            throw new RuntimeException("음수는 처리할 수 없습니다.");
        }
    }

    private boolean isNumeric(String inputNumber){
        return NUMERIC.matcher(inputNumber).matches();
    }

    private boolean isPositive(String inputNumber){
        int number = Integer.parseInt(inputNumber);
        return (number > 0);
    }


}
