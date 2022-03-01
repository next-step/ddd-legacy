package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CarTest {


    @DisplayName("자동차의 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("동해물과 백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다.")
    @Test
    void forward() {
        final Car car = new Car("yun", 0);
        car.move(new ForwardMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 전진하지 않는다..")
    @Test
    void hold() {
        final Car car = new Car("yun", 0);
        car.move(new HoldMovingStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}