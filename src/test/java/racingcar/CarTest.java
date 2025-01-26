package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {

    @DisplayName("자동차 이름은 5자 이하여야 합니다.")
    @Test
    void createCar() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("123456"));
    }

    @DisplayName("숫자가 4 이상일 때만 전진 합니다.")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @ParameterizedTest
    void forwardCar(int condition) {
        Car car = new Car("car");
        car.move(new ForwardStrategy(condition));
        Assertions.assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만일 때는 전진하지 않습니다.")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stopCar(int condition) {
        Car car = new Car("car");
        car.move(new ForwardStrategy(condition));
        Assertions.assertThat(car.getPosition()).isEqualTo(0);
    }
}