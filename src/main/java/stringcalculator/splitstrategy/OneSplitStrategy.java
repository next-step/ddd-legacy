package stringcalculator.splitstrategy;

import stringcalculator.SplitStrategy;

public class OneSplitStrategy implements SplitStrategy {
    @Override
    public String[] split(String text) {
        return new String[] {text};
    }

    @Override
    public boolean canSplit(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
