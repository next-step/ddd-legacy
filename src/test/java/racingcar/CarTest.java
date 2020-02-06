package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("자동차를 생성한다.")
    void create() {
        Car car = new Car("dong");
        assertThat(car).isNotNull();

    }

    @Test
    @DisplayName("자동차의 이름이 5글자 이상시 에러.")
    void createFail() {
        assertThrows(IllegalArgumentException.class, () -> new Car("dongchul"));
    }

    @Test
    @DisplayName("자동차가 조건에 따라 움직였는지.")
    void carMove() {
        Car car = new Car("jason");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);

    }
}