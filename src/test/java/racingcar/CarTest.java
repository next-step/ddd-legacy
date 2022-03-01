package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("나는야 자동차"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다.")
    @Test
    void forward() {
        final Car car = new Car("seul", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("3이하인 경우 자동차가 전진하지 않는다.")
    void hold() {
        final Car car = new Car("seul", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }
}