package calculator;

public class StringCalculator {

    public int add(String text) {
        int sum = 0;
        int num = 0;

        for (char c : text.toCharArray()) {
            if (c == ',' || c == ':') {
                sum += num;
                num = 0;
            } else {
                num = num * 10 + (c - '0');
            }
        }

        return sum + num;
    }

}
