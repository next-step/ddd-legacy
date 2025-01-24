package calculator;

import java.util.regex.Pattern;

public class SimpleTextSplitter implements TextSplitter {

    @Override
    public String[] splitText(InputText inputText) {
        Pattern pattern = Pattern.compile("[%s]".formatted(inputText.delimiters()));
        return pattern.split(inputText.numberText());
    }

}
