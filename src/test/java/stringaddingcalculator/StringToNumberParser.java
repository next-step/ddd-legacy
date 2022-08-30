package stringaddingcalculator;

import java.util.List;

public interface StringToNumberParser {
    boolean isSupport(final String source);

    List<Integer> parse(final String source);
}
