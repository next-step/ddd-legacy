package calculator.source.splitter;

import java.util.List;

public class FixedSplitter implements StringSplitter {
    private final String delimiter;

    public FixedSplitter(final String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public List<String> split(final String input) {
        String[] tokens = input.split(delimiter);
        return List.of(tokens);
    }

}
