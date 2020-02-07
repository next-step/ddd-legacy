package calculator;

import io.micrometer.core.instrument.util.StringUtils;

public class Calculator {
    private String text;
    private Splitter splitter;

    public Calculator(String text) {
        this.text = text;
    }

    public int sum() {
        int sum = 0;
        splitter = new Splitter(this.text);

        if(StringUtils.isBlank(this.text)) return sum;

        String[] textArray = splitter.getSplitText();

        for(String o : textArray) {
            int number = Integer.parseInt(o);
            if(number < 0) throw new RuntimeException();
            sum = sum + Integer.parseInt(o);
        }

        return sum;
    }
}
