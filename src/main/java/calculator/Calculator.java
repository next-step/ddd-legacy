package calculator;

public class Calculator {
    private String text;
    private static final String TEXT_SPERATOR_1 = ",";
    private static final String TEXT_SPERATOR_2 = ":";


    public Calculator(String text) {
        this.text = text;
    }

    public int sum() {
        int sum = 0;

        if(this.text == null) return sum;
        if(this.text.isEmpty()) return sum;

        String[] textArray = this.text.split(String.format("%s|%s", TEXT_SPERATOR_1, TEXT_SPERATOR_2));

        for(String o : textArray) {
            sum = sum + Integer.parseInt(o);
        }

        return sum;
    }
}
