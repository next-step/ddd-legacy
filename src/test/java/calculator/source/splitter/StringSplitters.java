package calculator.source.splitter;

import java.util.ArrayList;
import java.util.List;

public class StringSplitters {
    private final List<StringSplitter> splitters = new ArrayList<>();

    private StringSplitters(final List<StringSplitter> splitters) {
        this.splitters.addAll(splitters);
    }

    public StringSplitters() {
    }

    public StringSplitters add(final StringSplitter splitter) {
        this.splitters.add(splitter);
        return new StringSplitters(this.splitters);
    }

    public List<String> split(final String input) {
        for (StringSplitter splitter : splitters) {
            List<String> result = splitter.split(input);
            if (result != null && !result.isEmpty()) {
                return result;
            }
        }
        return List.of(input);
    }
}
