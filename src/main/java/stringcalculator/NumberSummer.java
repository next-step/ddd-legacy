package stringcalculator;

public class NumberSummer {

    public static int sum (String[] numbers) {
        int result = 0;

        for(String number : numbers) {
            result += Integer.parseInt(number);
        }

        return result;
    }
}
