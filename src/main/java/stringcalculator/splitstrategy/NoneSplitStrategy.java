package stringcalculator.splitstrategy;

import stringcalculator.SplitStrategy;

public class NoneSplitStrategy implements SplitStrategy {
    @Override
    public String[] split(String text) {
        return new String[] {"0"} ;
    }

    @Override
    public boolean canSplit(String text) {
        return text == null || text.isBlank();
    }
}
