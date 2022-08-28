package calculator.source;

import calculator.source.splitter.StringSplitter;

import java.util.List;

public class StringCalculator {
    private final List<StringSplitter> splitters;

    public StringCalculator(final List<StringSplitter> splitters) {
        this.splitters = splitters;
    }

    public Number plus(final String input) {
        if(input==null || input.isBlank()){
            return new Number(0);
        }
        return split(input)
                .stream()
                .map(Number::new)
                .reduce(Number::plus)
                .orElseThrow(() -> new RuntimeException("ss"));
    }

    private List<String> split(final String input) {
        for (StringSplitter splitter : splitters) {
            List<String> result = splitter.split(input);
            if (result != null && !result.isEmpty()) {
                return result;
            }
        }
        return List.of(input);
    }
}
