package stringaddcalculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SeparatorTest {
    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열에서 숫자를 분리한다")
    @ValueSource(strings = {"1,2,3", "1,2:3"})
    @ParameterizedTest
    void separate(final String expression) {
        Separator separator = new Separator();

        int[] answer = separator.separate(expression);

        assertThat(answer).containsExactly(1, 2, 3);
    }
}
