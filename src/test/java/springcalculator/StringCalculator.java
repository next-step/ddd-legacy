package springcalculator;

public class StringCalculator {
    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        if (!input.contains(",") && !input.contains(":")) {
            return Integer.parseInt(input);
        }

        String[] numbers = input.split(",|:");
        int sum = 0;
        for (String number : numbers) {
            sum += Integer.parseInt(number);
        }

        return sum;
    }
}
