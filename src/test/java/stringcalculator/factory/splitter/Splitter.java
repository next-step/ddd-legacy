package stringcalculator.factory.splitter;

import java.util.List;

@FunctionalInterface
public interface Splitter {

    List<String> split(final String value);
}
