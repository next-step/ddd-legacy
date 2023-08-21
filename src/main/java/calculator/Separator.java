package calculator;

public class Separator {
    private Separator() {
        throw new IllegalStateException("구분자는 생성 될 수 없습니다.");
    }

    public static NumberStrings separate(final TargetString targetString) {
        NumberAppender numberAppender = new NumberAppender(targetString);
        for (char ch : targetString.toCharArray()) {
            numberAppender.appendToNumberStrings(ch);
        }
        return numberAppender.getNumberStrings();
    }
}
