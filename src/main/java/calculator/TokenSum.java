package calculator;

public class TokenSum {
//    String[] tokens = findTokens(text);
//    int sum = 0;
//
//        for (String token : tokens) {
//        int num = Integer.parseInt(token);
//        validate(num);
//        sum += num;
//    }
//
//        return sum;

    private int sum;

    TokenSum(String[] tokens) {
        for (String token : tokens) {
            int num = Integer.parseInt(token);
            validate(num);
            sum += num;
        }
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
