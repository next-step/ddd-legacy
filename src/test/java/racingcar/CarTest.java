package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

public class CarTest {
    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructor() {
        assertThatCode(() -> new Car("name", 0))
                .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty_name(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name, 0));
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor_with_invalid_params() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("잘못된 차이름입니다.", 0));
    }

    @DisplayName("자동차는 움직일 수 없는 경우에만 정지한다.")
    @Test
    void check_car_movable() {
        // 작성예정
    }

}
