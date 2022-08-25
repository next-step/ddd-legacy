package calculator;

import java.util.Objects;

public class StringCalculator {

    private final String string;

    public StringCalculator(String s) {
        this.string = s;
    }

    public int calculate() {
        if (Objects.equals(string, "")) {
            return 0;
        }
        return 1;
    }

}
