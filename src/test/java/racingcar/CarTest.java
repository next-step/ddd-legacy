package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차 이름은 다섯 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(
                () -> new Car("하나둘셋넷다섯")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다.")
    @Test
    void move() {
        final Car car = new Car("abc", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 전진하지 않는다.")
    @Test
    void notMove() {
        final Car car = new Car("abc", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }
}