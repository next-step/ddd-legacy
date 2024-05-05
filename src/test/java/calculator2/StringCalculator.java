package calculator2;


import java.util.stream.Stream;

public class StringCalculator {

    private final TextInputHandler textInputHandler = new TextInputHandler();
    private final NumberMapper numberMapper = new NumberMapper();
    private final Calculator calculator = new Calculator();

    public int add(String text) {
        if (textInputHandler.isBlank(text)) {
            return 0;
        }
        String[] tokens = textInputHandler.tokenize(text);
        Stream<Number> numbers = numberMapper.toNumbers(tokens);
        return calculator.sum(numbers);
    }
}
