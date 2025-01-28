package mission.step1;

public class Parser implements ParserStrategy {
    private static final String DELIMITER = "[,:]";

    @Override
    public String[] splitWithDelimiter(String input) {
        return input.split(DELIMITER);
    }
}
