package calculator;

import java.util.Objects;

public record InputText(String numberText, String delimiters) {
    public InputText {
        Objects.requireNonNull(numberText, "numberText는 null이 될 수 없습니다.");
        Objects.requireNonNull(delimiters, "delimiters null이 될 수 없습니다.");
    }
}
