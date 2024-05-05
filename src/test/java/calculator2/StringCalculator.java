package calculator2;


import java.util.stream.Stream;

public class StringCalculator {

    private TextInputHandler textInputHandler = new TextInputHandler();
    private NumberMapper numberMapper = new NumberMapper();
    private Calculator calculator = new Calculator();

    public int add(String text) {
        if (textInputHandler.isBlank(text)) {
            return 0;
        }
        String[] tokens = textInputHandler.tokenize(text);
        Stream<Number> numbers = numberMapper.toNumbers(tokens);
        return calculator.sum(numbers);
    }
}
