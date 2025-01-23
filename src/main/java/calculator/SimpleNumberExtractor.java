package calculator;

public class SimpleNumberExtractor implements NumberExtractor {

    @Override
    public String[] extractNumber(String text, String delimiters) {
        return text.split(buildDelimiterPattern(delimiters));
    }

    private String buildDelimiterPattern(String delimiters) {
        return String.join("|", delimiters.split(""));
    }

}
