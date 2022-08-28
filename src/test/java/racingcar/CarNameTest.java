package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import racingcar.source.CarName;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class CarNameTest {

    @DisplayName("자동차 이름은 5글자 이하다")
    @Test
    void name() {
        Assertions.assertThatNoException()
                .isThrownBy(() -> new CarName("abcde"));
    }

    @DisplayName("자동차 이름이 5글자를 넘으면 IllegalArgumentException이 발생한다.")
    @Test
    void name_exception() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CarName("abcdefgh"));
    }

    @DisplayName("자동차 이름이 null이나 blank이면 IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void name_null_exception(String value) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new CarName(value));
    }
}
