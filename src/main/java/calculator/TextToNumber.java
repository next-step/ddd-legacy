package calculator;

import java.util.List;
import java.util.stream.Collectors;

public class TextToNumber {
    private List<Number> numberList;

    public TextToNumber(String[] text) {
        this.numberList = this.convertNumberArray(new NumberParser(text));
    }

    private List<Number> convertNumberArray(NumberParser numberParser) {
        return numberParser.getNumberArray().stream()
                .map(Number::new)
                .collect(Collectors.toList());
    }

    public Integer sum() {
        return numberList.stream()
                .reduce(new Number(0), Number::sum)
                .getNumber();
    }
}
