package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;


public class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
    @Test
    void constructor() {
        // 5글자가 넘는 경우, IllegalArgumentException 발생
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> {
                    new Car("동해물과백두산이");
                }
        );

    }

    @DisplayName("숫자가 4 이상인 경우 자동차가 전진한다")
    @Test
    void move() {
        final Car car = new Car("k5");
        car.moving(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만인 경우 자동차는 정지한다")
    @Test
    void stop() {
        final Car car = new Car("k5");
        car.moving(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }

}
