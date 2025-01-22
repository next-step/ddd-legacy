package calculator;

import java.util.List;
import java.util.stream.Stream;

public class BodyValue {
    private final String body;
    public BodyValue(String body) {
        this.body = body;
    }

    public void validation(String delimiter) {
        if (!body.matches("^[0-9]+([" + delimiter + "][0-9]+)*$")) {
            throw new RuntimeException("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열 형식이 아닙니다.");
        }
    }

    public List<PositiveInteger> getPositiveIntegers(String delimiter) {
        String[] strings = body.split("[" + delimiter + "]");
        return Stream.of(strings)
                .map(PositiveInteger::of)
                .toList();
    }
}
