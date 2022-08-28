package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import racingcar.moving_strategy.AlwaysHoldMovingStrategy;
import racingcar.moving_strategy.AlwaysMoveMovingStrategy;

class CarTest {

    @DisplayName("자동차 name은 5글자로 설정할 수 없다.")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> new Car("12345"));
    }

    @DisplayName("자동차 name은 5글자를 넘을 수 없다.")
    @Test
    void constructor_long_name() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("123456"));
    }

    @DisplayName("자동차 name은 null이거나 empty일 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_or_empty_name(final String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차 name은 blank일 수 없다.")
    @ValueSource(strings = {"  ", "\t"})
    @ParameterizedTest
    void constructor_with_blank_name(final String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차 position은 음수일 수 없다.")
    @Test
    void constructor_position_not_negative() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("name", -1));
    }

    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        final Car car = new Car("car");
        car.move(new AlwaysMoveMovingStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 움직일 수 없는 경우 움직이지 않는다.")
    @Test
    void stop() {
        final Car car = new Car("car");
        car.move(new AlwaysHoldMovingStrategy());
        assertThat(car.getPosition()).isZero();
    }
}
