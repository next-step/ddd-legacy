package caculator.domain;

public class StringSplitter {

    public static final String DEFAULT_DELIMITER = "[,:]";

    private StringSplitter() {
        throw new AssertionError();
    }

    public static Numbers split(String stringNumbers) {
        String[] numbers = stringNumbers.split(DEFAULT_DELIMITER);
        return Numbers.from(numbers);
    }

}
