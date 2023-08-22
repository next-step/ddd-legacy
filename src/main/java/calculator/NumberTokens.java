package calculator;

import java.util.ArrayList;
import java.util.List;

public class NumberTokens {
    private final String[] tokens;

    NumberTokens(String[] tokens) {
        this.tokens = tokens;
    }

    public int generateSum() {
        List<PositiveNumber> positiveNumberList = new ArrayList<>();
        for (String token : tokens) {
            int num = Integer.parseInt(token);
            positiveNumberList.add(new PositiveNumber(num));
        }
        return new PositiveNumbers(positiveNumberList).sum();
    }
}
