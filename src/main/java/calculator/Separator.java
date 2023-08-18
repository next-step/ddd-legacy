package calculator;

public class Separator {
    private Separator() { }

    public static NumberStrings separate(final TargetString targetString) {
        NumberAppender numberAppender = new NumberAppender(targetString);
        for (char ch : targetString.toCharArray()) {
            numberAppender.appendToNumberStrings(ch);
        }
        return numberAppender.getNumberStrings();
    }
}
