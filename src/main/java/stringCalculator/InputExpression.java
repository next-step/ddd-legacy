package stringCalculator;


public record InputExpression(Delimiters delimiters, Numbers numbers) {

    public static InputExpression parse(final ParsingStrategy parsingStrategy, final String text) {
        if (emptyOrNull(text)) {
            return new InputExpression(Delimiters.ofDefaults(), Numbers.empty());
        }
       return parsingStrategy.parse(text);
    }

    private static boolean emptyOrNull(final String text) {
        return text == null || text.isEmpty();
    }
}


