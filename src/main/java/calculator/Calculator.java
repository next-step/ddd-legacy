package calculator;

import io.micrometer.core.instrument.util.StringUtils;

public class Calculator {
    private String text;
    private Spliter spliter;

    public Calculator(String text) {
        this.text = text;
    }

    public int sum() {
        int sum = 0;
        spliter = new Spliter();

        if(StringUtils.isBlank(this.text)) return sum;

        String[] textArray = spliter.splitText(this.text);

        for(String o : textArray) {
            int number = Integer.parseInt(o);
            if(number < 0) throw new RuntimeException();
            sum = sum + Integer.parseInt(o);
        }

        return sum;
    }
}
