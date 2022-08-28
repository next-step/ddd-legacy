package calculator.source;

import calculator.source.splitter.StringSplitters;

public class StringCalculator {
    private final StringSplitters splitters;

    public StringCalculator(final StringSplitters splitters) {
        this.splitters = splitters;
    }

    public Number plus(final String input) {
        if(input==null || input.isBlank()){
            return new Number(0);
        }
        return splitters.split(input)
                .stream()
                .map(Number::new)
                .reduce(Number::plus)
                .orElseThrow(() -> new RuntimeException("문자열 덧셈을 실패했습니다."));
    }

}
