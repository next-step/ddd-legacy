package stringaddcalculator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class SeparatorTest {
    private Separator separator;

    @BeforeEach
    void setup() {
        separator = new Separator();
    }

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열에서 숫자를 분리한다")
    @ValueSource(strings = {"1,2,3", "1,2:3"})
    @ParameterizedTest
    void separate(final String expression) {
        int[] answer = separator.separate(expression);

        assertThat(answer).containsExactly(1, 2, 3);
    }

    @DisplayName("빈 문자열 또는 null을 입력할 경우 예외가 발생한다")
    @NullAndEmptySource
    @ParameterizedTest
    void validate(final String expression) {
        assertThatThrownBy(() -> separator.separate(expression))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("구분자를 커스텀할 수 있다")
    @ValueSource(strings = {"//;\n1;2;3"})
    @ParameterizedTest
    void custom_delimiter(final String expression) {
        int[] answer = separator.separate(expression);

        assertThat(answer).containsExactly(1, 2, 3);
    }
}
