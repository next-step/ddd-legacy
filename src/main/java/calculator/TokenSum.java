package calculator;

public class TokenSum {
    private final int sum;

    TokenSum(String[] tokens) {
        sum = generateSum(tokens);
    }

    private int generateSum(String[] tokens) {
        int sum = 0;
        for (String token : tokens) {
            int num = Integer.parseInt(token);
            validate(num);
            sum += num;
        }
        return sum;
    }

    private void validate(final int num) {
        if (num < 0) {
            throw new RuntimeException();
        }
    }

    public int getSum() {
        return sum;
    }
}
