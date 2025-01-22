package calculator;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        int sum = 0;
        int num = 0;
        String delimiters = ",:";

        if (text.startsWith("//") && "\\n".equals(text.substring(3, 5))) {
            delimiters += text.charAt(2);
            text = text.substring(5);
        }

        for (char c : text.toCharArray()) {
            if (delimiters.indexOf(c) >= 0) {
                sum += num;
                num = 0;
            } else {
                num = num * 10 + (c - '0');
            }
        }

        return sum + num;
    }

}
