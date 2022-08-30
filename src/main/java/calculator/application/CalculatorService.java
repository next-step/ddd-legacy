package calculator.application;

import calculator.domain.AddNumber;
import calculator.domain.AddNumbers;
import calculator.domain.Separators;
import java.util.List;

public class CalculatorService {

    private final Separators separators;

    public CalculatorService() {
        this(Separators.generate());
    }

    private CalculatorService(Separators separators) {
        this.separators = separators;
    }

    public AddNumber add(String text) {
        List<String> texts = separators.splitText(text);
        AddNumbers numbers = AddNumbers.from(texts);
        return numbers.addAllNumbers();
    }
}
