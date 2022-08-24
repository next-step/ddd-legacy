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
        assertThatCode(() -> new Car("name"))
                .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차 이름은 5글자를 넘을수 없다.")
    @Test
    void constructor_with_illegal_name() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("5글자를 넘는 자동차 이름"));
    }

    @DisplayName("자동차 위치는 0보다 작을 수 없다.")
    @Test
    void constructor_with_illegal_position() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("myCar", -1));
    }

    @DisplayName("자동차는 움직일수 없는 경우 정지한다.")
    @Test
    void check_stop_by_position() {
        final int beforePosition = 0;
        Car stoppedCar = new Car("MyCar", beforePosition);
        stoppedCar.move((new StopStrategy()));

        assertThat(stoppedCar.getPosition()).isZero();
    }

    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void check_move_forward_by_position() {
        final int beforePosition = 4;
        Car movableCar = new Car("MyCar", beforePosition);

        movableCar.move((new ForwardStrategy()));
        assertThat(movableCar.getPosition()).isEqualTo(beforePosition + 1);
    }
}
