package calculator;


public class PositiveNumber {
    private String inputNumber;

    public PositiveNumber(String inputNumber){
        validationNumber(inputNumber);
        this.inputNumber = inputNumber;
    }

    public String getNumber(){
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
        return inputNumber.matches("[0-9]+");
    }
    private boolean isPositive(String inputNumber){
        int number = Integer.parseInt(inputNumber);
        return (number > 0);
    }


}
