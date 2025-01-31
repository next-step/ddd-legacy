package stringcalculator;

import java.util.Optional;

public class StringCalculator {


    public int add(String text) {
        if(text == null || text.isEmpty()) {
            return 0;
        }

        String[] numbers = text.split("[,:]");

        return sum(numbers);
    }

    private int sum (String[] numbers) {
        int result = 0;

        for(String number : numbers) {
            result += Integer.parseInt(number);
        }

        return result;
    }
}
