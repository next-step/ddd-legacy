package study;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @Test
    @DisplayName("값이 4 이상인 경우 자동차가 이동한다.")
    void move() {
        final Car car = new Car("우찬");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("값이 4 미만인 경우 자동차가 움직이지 않는다.")
    void notMove() {
        final Car car = new Car("우찬");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isZero();
    }
}
