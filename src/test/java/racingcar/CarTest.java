package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;


class CarTest {
    @DisplayName("자동차 이름은 5자 이하여야 합니다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("자동차이름다섯글자"));
    }

    @DisplayName("숫자가 4 미만일때 정지한다.")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(int condition) {
        Car car = new Car("자동차");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(0);
    }

    @DisplayName("숫자가 4이상일때 움직인다.")
    @ValueSource(ints = {4, 5, 6, 7})
    @ParameterizedTest
    void move(int condition) {
        Car car = new Car("자동차");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}