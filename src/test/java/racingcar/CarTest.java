package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CarTest {

    @DisplayName("자동차 이름이 5글자를 넘으면, IllegalArgumentException 이 발생한다.")
    @ParameterizedTest(name = "자동차 이름: {0}")
    @ValueSource(strings = {"123456", "abcdefghi"})
    void abnormalMove(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name))
                .withMessage("자동차 이름은 5 글자를 넘을 수 없습니다.");
    }

    @DisplayName("자동차 이름이 5글자를 넘지않으면, 정상적으로 Car 객체가 생성된다.")
    @ParameterizedTest(name = "자동차 이름: {0}")
    @ValueSource(strings = {"", "1", "12", "123", "1234"})
    void normalMove(String name) {
        assertDoesNotThrow(() -> new Car(name));
    }

    @DisplayName("true 값을 받으면 move 를 통해 자동차가 움직인다.")
    @Test
    void movable() {
        Car car = new Car("");
        car.move(() -> true);
        int movedPosition = 1;
        assertThat(car.getPosition())
                .isEqualTo(movedPosition);
    }

    @DisplayName("false 값을 받으면 move 를 통해 자동차가 움직이지 않는다.")
    @Test
    void unMovable() {
        Car car = new Car("");
        car.move(() -> false);
        int unMovedPosition = 0;
        assertThat(car.getPosition())
                .isEqualTo(unMovedPosition);
    }
}
