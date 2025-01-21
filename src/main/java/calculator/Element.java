package calculator;

import java.util.Objects;

public class Element {
    private final int element;

    private Element(final String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            this.element = 0;
            return;
        }
        if (text.trim().length() > 1) {
            throw new IllegalArgumentException("구분 후 한 자리수 숫자만 입력할 수 있습니다.");
        }
        try {
            this.element = Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아닙니다.");
        }
        if (this.element < 0) {
            throw new IllegalArgumentException("음수는 입력할 수 없습니다.");
        }
    }

    public static Element of(final String number) {
        return new Element(number);
    }

    public int getElement() {
        return element;
    }
}
