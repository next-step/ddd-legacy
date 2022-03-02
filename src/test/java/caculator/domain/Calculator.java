package caculator.domain;

public class Calculator {

    private Calculator() {
        throw new AssertionError();
    }

    public static int add(String stringNumbers) {
        Numbers numbers = StringSplitter.split(stringNumbers);
        return numbers.sum();
    }
}
