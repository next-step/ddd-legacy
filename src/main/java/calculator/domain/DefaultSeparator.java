package calculator.domain;

import java.util.List;

public class DefaultSeparator implements Separator {

    @Override
    public List<String> split(String text) {
        return List.of(text.split("[,:]"));
    }

    @Override
    public boolean isMatchWithText(String text) {
        return false;
    }
}
