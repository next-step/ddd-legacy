package racingcar;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {

    @DisplayName("자동차 이름은 5글자 이하이다")
    @ParameterizedTest
    @ValueSource(strings = {"a", "ab", "abc", "abcd", "abcde"})
    void constructor(final String name) {
        assertThatCode(() -> new Car(name))
                  .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
    @Test
    void constructor_with_max_size_name() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Car("abcdef"));
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다")
    @ParameterizedTest
    @NullAndEmptySource
    void constructor_with_empty_and_null_name(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Car(name));
    }

}
