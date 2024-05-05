package stringcalculator;

public class StringCalculator {

    private final StringValidation stringValidation;

    public StringCalculator(){
        this.stringValidation = new StringValidation();
    }

    public int add(String text) {
       return stringValidation.parseNumber(text);
    }

}
