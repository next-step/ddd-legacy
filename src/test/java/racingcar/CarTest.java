package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {
    @DisplayName("생성 조건을 확인한다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다")
    @Test
    void go() {
        final Car car = new Car("mandy");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직이지 않는다")
    @Test
    void stop() {
        final Car car = new Car("mandy");
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }
}