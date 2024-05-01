package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"))
        ;
    }

    @DisplayName("값이 4 이상인 경우 자동차는 전진한다.")
    @Test
    void move() {
        final Car car = new Car("홍길동");
        car.move(4);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다.")
    @Test
    void stop() {
        final Car car = new Car("홍길동");
        car.move(3);
        assertThat(car.position()).isZero();
    }


}
