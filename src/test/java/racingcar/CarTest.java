package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다")
    @Test
    void move() {
        Car car = new Car("김지오");
        car.move(() -> true);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다")
    @Test
    void stop() {
        Car car = new Car("김지오");
        car.move(() -> false);
        assertThat(car.position()).isEqualTo(0);
    }
}
