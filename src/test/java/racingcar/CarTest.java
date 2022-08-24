package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {
    @ParameterizedTest
    @ValueSource(strings = {"c", "ca", "car", "cars"})
    @DisplayName("자동차의 이름은 5글자 이하이다.")
    void validCarName(final String name) {
        // when
        // then
        Assertions.assertThatCode(() -> new Car(name)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("자동차의 이름은 5 글자를 넘을 수 없다.")
    void notValidCarName() {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("이름이 5자가 넘습니다."));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("자동차 이름은 비어있을 수 없다.")
    void carNameEmptyOrNull(final String name) {
        assertThatThrownBy(() -> new Car(name))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("값이 4 이상인 경우에 자동차가 움직인다.")
    void moveCar() {
        // given
        final Car car = new Car("car", 0);
        // when
        car.move(new ForwardStrategy());
        // then
        assertThat(car.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("값이 4 보다 작을 경우에 자동차가 정지한다.")
    void stopCar() {
        // given
        final Car car = new Car("car", 0);
        // when
        car.move(new HoldStrategy());
        // then
        assertThat(car.position()).isZero();
    }
}
