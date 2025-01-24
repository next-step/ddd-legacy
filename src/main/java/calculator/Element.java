package calculator;

import java.util.Objects;

public class Element {
    private final int element;

    private Element(final Integer number) {
        if (Objects.isNull(number)) {
            this.element = 0;
            return;
        }
        if (number < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
        if (number > 9) {
            throw new IllegalArgumentException("한 자리 수 이상은 입력할 수 없습니다.");
        }
        this.element = number;
    }

    public static Element of(final Integer number) {
        return new Element(number);
    }

    public int getElement() {
        return element;
    }
}
