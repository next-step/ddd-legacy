package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        String overFiveLengthName = "일이삼사오육";
        assertThatThrownBy(() -> {
            new Car(overFiveLengthName);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 값이 4 이상일 경우에만 움직인다.")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10})
    @ParameterizedTest
    void move(final int value) {
        final Car car = new Car("test");
        car.move(value);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("자동차는 값이 4 미만인 경우에는 정지한다.")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int value) {
        final Car car = new Car("test");
        car.move(value);
        assertThat(car.position()).isEqualTo(0);
    }
}