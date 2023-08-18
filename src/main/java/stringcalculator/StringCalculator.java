package stringcalculator;

public class StringCalculator {

    public int add(String text) {
        String[] spilt = SplitStrategies.spilt(text);
        Positives positives = new Positives(spilt);
        return positives.getSum();
    }

}
