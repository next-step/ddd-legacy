package calculator;

import java.util.List;

public class TextDelimiters {

    private final List<TextDelimiter> textDelimiters;

    public TextDelimiters() {
        this(List.of(new FixedTextDelimiter(), new DynaminTextDelimiter()));
    }

    public TextDelimiters(final List<TextDelimiter> textDelimiters) {
        this.textDelimiters = textDelimiters;
    }

    public List<String> split(final String text) {
        return textDelimiters.stream()
                .filter(textDelimiter -> textDelimiter.isSupport(text))
                .findFirst()
                .map(textDelimiter -> textDelimiter.split(text))
                .orElseGet(() -> List.of(text));
    }
}
