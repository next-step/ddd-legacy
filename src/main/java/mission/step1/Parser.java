package mission.step1;

public class Parser implements ParserStrategy {
    private static final String DELIMITER = "[,:]";

    @Override
    public String[] splitWithDelimiter(String input) {
        isDefaultParser(input);
        return input.split(DELIMITER);
    }

    public void isDefaultParser(String input) {
        Character c = input.charAt(0);
        if (!Character.isDigit(c)) {
            throw new RuntimeException("커스텀 구분자 형식이 올바르지 않습니다");
        }
    }
}
