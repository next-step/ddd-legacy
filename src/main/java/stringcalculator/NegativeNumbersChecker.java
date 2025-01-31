package stringcalculator;

public class NegativeNumbersChecker {

    public static String[] checkNumbers(String[] numbers) {
        for(String number : numbers){
            int num = Integer.parseInt(number);
            checkNumber(num);
        }

        return numbers;
    }

    private static void checkNumber(int num) {
        if(num < 0){
            throw new RuntimeException();
        }
    }
}
