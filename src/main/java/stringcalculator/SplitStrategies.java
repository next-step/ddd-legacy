package stringcalculator;

import stringcalculator.splitstrategy.CommaAndColonSplitStrategy;
import stringcalculator.splitstrategy.CustomSplitStrategy;
import stringcalculator.splitstrategy.NoneSplitStrategy;
import stringcalculator.splitstrategy.OneSplitStrategy;

import java.util.Arrays;
import java.util.List;

public class SplitStrategies {

    private static final List<SplitStrategy> strategies
            = Arrays.asList(
            new NoneSplitStrategy()
            , new OneSplitStrategy()
            , new CommaAndColonSplitStrategy()
            , new CustomSplitStrategy());


    public static String[] spilt(String text) {
        return strategies.stream()
                .filter(strategy -> strategy.canSplit(text))
                .findAny()
                .map(strategy -> strategy.split(text))
                .orElseThrow(() -> new RuntimeException(
                        StringCalculatorExceptionMessage.CANNOT_CALCULATE.getMessage()));
    }

}
