package calculator;

import java.util.List;


public class TextToNumber {
    private List<Number> numberList;

    public TextToNumber(String[] text) {
        this.numberList = this.getNumberArray(new IntegerParser(text));
    }

    private List<Number> getNumberArray(IntegerParser integerParser) {
        return integerParser.convertNumberArray();
    }

    public Integer sum() {
        return numberList.stream()
                .reduce(new Number(0), Number::sum)
                .getNumber();
    }
}
