package calculator;

public class NumberTokens {
    private final String[] tokens;

    NumberTokens(String[] tokens) {
        this.tokens = tokens;
    }

    public int generateSum() {
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
}
