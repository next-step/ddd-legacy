package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException 이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {

    @Test
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    void constructor() {

        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    @DisplayName("자동차가 전진한다.")
    void forward() {

        final Car car = new Car("kyu");
        car.move(new ForwardStrategy());

        assertThat(car.getPosition()).isEqualTo(1);

    }

    @Test
    @DisplayName("자동차가 움직이지 않는다.")
    void stop() {

        final Car car = new Car("kyu");
        car.move(new HoldStrategy());

        assertThat(car.getPosition()).isZero();

    }
}