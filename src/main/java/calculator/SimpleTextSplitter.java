package calculator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class SimpleTextSplitter implements TextSplitter {

    private static final Map<String, Pattern> patternCache = new ConcurrentHashMap<>();


    @Override
    public Numbers splitText(InputText inputText) {
        if (inputText.delimiters().isBlank()) {
            return new Numbers(new String[]{inputText.numberText()});
        }

        Pattern pattern = patternCache.computeIfAbsent(
                inputText.delimiters(),
                key -> Pattern.compile("[%s]".formatted(key))
        );
        return new Numbers(pattern.split(inputText.numberText()));
    }

}
