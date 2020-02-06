package racingcar;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

class CarTest {

    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("jason");
        Assertions.assertThat(car).isNotNull();
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"123456", "1234567"})
    void carNameMaxLength(String name) {
        Assertions.assertThatThrownBy(() -> new Car(name))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        Car car = new Car("jjy");
        car.move(() -> true);

        Assertions.assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직이지 않는다..")
    @Test
    void noneMove() {
        Car car = new Car("jjy");
        car.move(() -> false);

        Assertions.assertThat(car.getPosition()).isEqualTo(0);
    }


}