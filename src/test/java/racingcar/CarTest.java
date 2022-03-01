package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {
    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    void constructor() {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자동차가 전진한다.")
    void forward() {
        final Car car = new Car("Hanee", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차가 전진하지 않는다.")
    void hole() {
        final Car car = new Car("Hanee", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}