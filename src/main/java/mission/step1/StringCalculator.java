package mission.step1;

public class StringCalculator {

    public static final String DELIMITER = "[,:]";

    public String[] splitWithDelimiter(String expression) {
        return expression.split(DELIMITER);
    }
}
