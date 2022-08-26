package calculator.domain;

import java.util.Arrays;
import java.util.Objects;

public enum Separator {
    REST(","),
    COLON(":");

    private final String text;

    Separator(String text) {
        this.text = text;
    }

    public static Separator findByText(String text) {
        return Arrays.stream(values())
            .filter(it -> Objects.equals(it.text, text))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("일치하는 구분자를 찾을 수 없습니다."));
    }
}
