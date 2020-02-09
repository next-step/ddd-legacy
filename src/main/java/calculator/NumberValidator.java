package calculator;

public class NumberValidator {

    public static int validate(Integer number){
        if(number < 0) throw new RuntimeException("Number must be positive");
        return number;
    }
}
