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
        Car car = new Car("test");
        assertThat(car).isInstanceOf(Car.class);
    }

    @DisplayName("자동차 이름이 5 글자가 넘는 경우, IllegalArgumentException이 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"동해물과 백두산이", "산이 마르고 닳도록"})
    void name(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(()-> new Car(name));
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        final Car car = new Car("자동차");
        car.move(() -> {
            return true;
        });
        assertThat(car.getPosition()).isEqualTo(1);

    }
}