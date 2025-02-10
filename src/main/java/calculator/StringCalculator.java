package calculator;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return sum(parse(text));
    }

    private String[] parse(String text) {
        return text.split("[,:]");
    }

    private int sum(String[] numbers) {
        int total = 0;
        for (String number : numbers) {
            total += Integer.parseInt(number);
        }
        return total;
    }
}
