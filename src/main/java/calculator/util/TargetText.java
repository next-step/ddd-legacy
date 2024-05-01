package calculator.util;

public class TargetText {

    private final String targetText;

    public TargetText(String targetText) {
        validateSplitText(targetText);
        this.targetText = targetText;
    }

    private static void validateSplitText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text is Null. Can't Split");
        }

        if (text.isEmpty()) {
            throw new IllegalArgumentException("Text isEmpty. Can't Split");
        }
    }

    public String[] split(String delimiter) {
        return targetText.split(delimiter);
    }
}
