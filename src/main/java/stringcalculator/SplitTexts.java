package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SplitTexts {

    private final List<String> texts;

    public SplitTexts(List<String> texts) {
        this.texts = texts;
    }

    public SplitTexts(String[] numbers) {
        this(Arrays.asList(numbers));
    }

    public SplitTexts() {
        this.texts = Collections.emptyList();
    }

    public List<String> getValues() {
        return this.texts;
    }

    public boolean isEmpty() {
        return this.texts.isEmpty();
    }
}
