package calculator.strategy;

import java.util.List;

public interface TextDelimiter {
    boolean isSupport(String text);

    List<String> split(String text);
}
