package calculator;

public class NumberValidator {

    public void validate(int value){
        positive(value);
    }

    private void positive(int value) {
        if(value < 0){
            throw new RuntimeException("not positive number");
        }
    }
}
