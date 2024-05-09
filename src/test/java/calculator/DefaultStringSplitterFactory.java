package calculator;

import java.util.List;

class DefaultStringSplitterFactory implements StringSplitterFactory {

    private static final List<StringSplitter> SPLITTERS = List.of(
        new CustomStringSplitter()
    );

    @Override
    public StringSplitter create(final String value) {
        if (value == null) {
            return new DefaultStringSplitter();
        }
        return SPLITTERS.stream()
            .filter(splitter -> splitter.support(value))
            .findAny()
            .orElse(new DefaultStringSplitter());
    }
}
