package calculator;

public class StringParser {
    private final String delimiter;
    private final String numberText;

    private StringParser(String delimiter, String numberText) {
        this.delimiter = delimiter;
        this.numberText = numberText;
    }

    public static StringParser of(String text) {
        String delimiter = ",|\\:";
        String[] splitText = text.split("\n");
        if (splitText.length > 1) {
            delimiter += String.format("|\\%s", splitText[0].charAt(2));
        }
        String numberText = splitText.length > 1
                ? splitText[1]
                : splitText[0];
        return new StringParser(delimiter, numberText);
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getNumberText() {
        return numberText;
    }
}
