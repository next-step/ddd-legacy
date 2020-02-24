package calculator;

import java.util.List;
import java.util.stream.Collectors;

public class TextToNumber {
    private List<Number> numberList;

    public TextToNumber(String[] text) {
        this.numberList = this.parseToNumber(new NumberParser(text).getNumberArray());
    }

    private List<Number> parseToNumber(List<Integer> values) {
        return values.stream()
                .map(Number::new)
                .collect(Collectors.toList());
    }

    public Integer sum() {
        return numberList.stream()
                .reduce(new Number(0), Number::sum)
                .getNumber();
    }
}
