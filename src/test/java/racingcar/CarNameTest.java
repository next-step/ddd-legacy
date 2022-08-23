package racingcar;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class CarNameTest {

    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void name() {
        assertThatCode(() -> new CarName("최현구"))
            .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 null이거나 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nameNullOrBlankException(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new CarName(name));
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void nameLengthException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new CarName("제이슨의 감미로운 목소리"));
    }
}
