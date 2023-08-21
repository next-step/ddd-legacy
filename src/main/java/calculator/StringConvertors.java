package calculator;

import javax.transaction.NotSupportedException;
import java.util.List;

public class StringConvertors {

    List<StingConvertor> stingConvertors;

    public StringConvertors() {
        this.stingConvertors = List.of(
                new ZeroStingConvertor(),
                new NumberStingConvertor(),
                new SplitStingConvertor(),
                new CustomSplitStingConvertor()
        );
    }

    public PositiveNumbers convert(String text) throws NotSupportedException {
        return stingConvertors.stream()
                .filter(calculatorPolicy -> calculatorPolicy.isSupport(text))
                .map(calculatorPolicy -> calculatorPolicy.calculate(text))
                .findAny()
                .orElseThrow(NotSupportedException::new);
    }

}
