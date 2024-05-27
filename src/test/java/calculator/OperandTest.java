package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OperandTest {

    @ParameterizedTest
    @DisplayName("음수로 생성 할 수는 없다.")
    @ValueSource(strings = {"-1", "-10"})
    public void 음수_예외_처리(Long value) throws Exception {
        assertThatThrownBy(() -> new Operand(value)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("양수 이외로는 생성 할 수는 없다.")
    @NullSource
    @ValueSource(strings = {"?", " ", "!"})
    public void 기호_예외_처리(String value) throws Exception {
        assertThatThrownBy(() -> new Operand(value)).isInstanceOf(IllegalArgumentException.class);
    }


}
