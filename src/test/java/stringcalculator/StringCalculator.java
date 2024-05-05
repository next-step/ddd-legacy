package stringcalculator;

public class StringCalculator {

    private String text;

    private final StringValidation stringValidation;

    public StringCalculator(){
        this.stringValidation = new StringValidation();
    }

    public int add(String text) {
       return stringValidation.parseNumber(text);
    }

}
