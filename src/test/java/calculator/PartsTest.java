package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

class PartsTest {

    @DisplayName("문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "123"})
    void constructor(String input) {
        Parts parts = new Parts(input);
        assertThat(parts.parts()).containsExactly(input);
    }

    @DisplayName("음수, 숫자 이외의 값이 포함되면 생성을 실패한다")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "l", "a"})
    void constructor_fail(String input) {
        assertThatRuntimeException()
                .isThrownBy(() -> new Parts(input));
    }
}
