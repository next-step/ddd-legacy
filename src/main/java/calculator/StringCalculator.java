package calculator;

import java.util.Objects;

public class StringCalculator {
    private int result;

    public StringCalculator(){
        this.result = 0;
    }

    public int add(String text){

        if(Objects.isNull(text) || text.isBlank()){
            return 0;
        }

        String[] numbers = text.split(",|:");

        for(String number : numbers){
            result += Integer.parseInt(number);
        }


        return result;
    }
}
