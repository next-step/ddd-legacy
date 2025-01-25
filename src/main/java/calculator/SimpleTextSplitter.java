package calculator;

import java.util.regex.Pattern;

public class SimpleTextSplitter implements TextSplitter {

    @Override
    public StringNumbers splitText(InputText inputText) {
        if (inputText.delimiters().isBlank()) {
            return new StringNumbers(new String[]{inputText.numberText()});
        }

        Pattern pattern = Pattern.compile("[%s]".formatted(inputText.delimiters()));
        return new StringNumbers(pattern.split(inputText.numberText()));
    }

}
