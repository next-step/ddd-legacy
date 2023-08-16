package stringcalculator;

public class StringCalculator {

    private final SplitStrategies strategies = new SplitStrategies();
    public int add(String text) {
        String[] spilt = strategies.spilt(text);
        Positives positives = new Positives(spilt);
        return positives.getSum();
    }

}
