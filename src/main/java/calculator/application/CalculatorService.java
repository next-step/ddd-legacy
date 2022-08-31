package calculator.application;

import calculator.domain.Operand;
import calculator.domain.Operands;
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

    public Operand add(String text) {
        List<String> texts = separators.splitText(text);
        Operands numbers = Operands.from(texts);
        return numbers.addAll();
    }
}
