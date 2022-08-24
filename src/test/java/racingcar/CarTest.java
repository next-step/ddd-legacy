package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatCode;

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

    @DisplayName("자동차는 움직일 수 없는 경우에는 정지한다.")
    @Test
    void check_car_stop() {
        int beforePosition = 1;
        Car stoppedCar = new Car("MyCar", beforePosition);

        assertThat(stoppedCar.move((new StopStrategy())).isStopStatus(beforePosition))
                .isFalse();
    }

    @DisplayName("자동차는 움직일 수 있는 경우에는 이동한다.")
    @Test
    void check_car_moveForward() {
        int beforePosition = 4;
        Car movableCar = new Car("MyCar", beforePosition);

        assertThat(movableCar.move((new ForwardStrategy())).isMoveForwardStatus(beforePosition))
                .isTrue();
    }

    @DisplayName("자동차는 위치는 0보다 작을 수 없다.")
    @Test
    void move_car_invalid_position() {
        int invalidPosition = -1;
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("MyCar", invalidPosition));
    }
}
