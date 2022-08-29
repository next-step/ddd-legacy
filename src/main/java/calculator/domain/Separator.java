package calculator.domain;

import java.util.List;

public interface Separator {

    public List<String> split(String text);

    public boolean isMatchWithText(String text);
}
