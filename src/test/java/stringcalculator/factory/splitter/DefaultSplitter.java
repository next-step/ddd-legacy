package stringcalculator.factory.splitter;

import java.util.List;

public class DefaultSplitter implements Splitter {

    private static final String DELIMITER = ",|:";

    @Override
    public List<String> split(String value) {
        return List.of(value.split(DELIMITER));
    }
}
