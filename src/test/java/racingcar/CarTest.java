package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class CarTest {
    @DisplayName("자동차 이름은 5글자이하이다.")
    @Test
    void constructor() {
        Assertions.assertThatCode(() -> new Car("5글자"))
                .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor_with_illegal_name() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("5글자 이상이다"));
    }

    @DisplayName("자동차 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty_name(final String name) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차가 움직일 수 있는 경우 이동 이동한다")
    @Test
    void move() {
        final Car car = new Car("test", 0);
        car.move(new ForwardStrategy());
        Assertions.assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직일 수 없는 경우 멈춘다")
    @Test
    void stop() {
        final Car car = new Car("test", 0);
        car.move(new HoldStrategy());
        Assertions.assertThat(car.getPosition()).isEqualTo(0);
    }
}
