package springcalculator;

import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
    private List<Integer> numbers;

    public StringCalculator(String input) {
        this.numbers = new ArrayList<>();
        parseInput(input);
    }

    public int add() {
        int sum = 0;
        for (int number : this.numbers) {
            sum += number;
        }
        return sum;
    }

    private void parseInput(String input) {
        if (input == null || input.isEmpty()) {
            return;
        }

        if (!input.contains(",") && !input.contains(":")) {
            this.numbers.add(Integer.parseInt(input));
            return;
        }

        String[] tokens = input.split("[,:]");
        for (String token : tokens) {
            this.numbers.add(Integer.parseInt(token));
        }
    }
}
