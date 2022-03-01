package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다")
    @Test
    void forward() {
        Car car = new Car("jang", 0);
        car.move(new ForwardStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 전진하지 않는다")
    @Test
    void hold() {
        Car car = new Car("jang", 0);
        car.move(new HoldStrategy());

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
