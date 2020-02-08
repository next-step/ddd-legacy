package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CarTest {

    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("james");
        assertThat(car).isNotNull();
    }

    @DisplayName("5 글자가 넘는 경우, IllegalArgumentException이 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"aaaaaa", "bbbbbb"})
    void name(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> {
                    new Car(name);
                });
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        final Car car = new Car("james");
//        car.move(new TestMovingStrategy());
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);

    }
}