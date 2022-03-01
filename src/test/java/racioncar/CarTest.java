package racioncar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void name() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Car("aaaaaa"));
    }

    @DisplayName("4 이상인 경우 자동차가 전진한다.")
    @Test
    void move() {
        final Car car = new Car("aaaaa", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("3 이하인 경우 자동차가 전진하지 않는다.")
    @Test
    void notMove() {
        final Car car = new Car("aaaaa", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
