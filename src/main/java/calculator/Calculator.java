package calculator;

import java.util.StringTokenizer;

public class Calculator {

    public int calculate(String value) {
        int sum = 0;

        if (value == null || value.isEmpty()) {
            return 0;
        }

        if (value.matches("[\"']+")) {
            return 0;
        }

        StringTokenizer st = new StringTokenizer(value, ",:");
        while (st.hasMoreElements()) {
            String token = st.nextToken().trim();
            sum += Integer.parseInt(token);
        }
        return sum;
    }
}
