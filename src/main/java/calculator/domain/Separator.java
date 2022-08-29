package calculator.domain;

import java.util.List;

public interface Separator {

    List<String> split(String text);

    boolean isMatchWithText(String text);
}
