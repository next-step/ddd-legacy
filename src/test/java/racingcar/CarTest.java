package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("값이 4 이상일 경우 자동차는 전진한다")
    @Test
    void move() {
        final var car = new Car("강지우");
        car.move(4);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("값이 4 미만일 경우 자동차는 움직이지 않는다")
    @Test
    void stop() {
        final var car = new Car("강지우");
        car.move(3);
        assertThat(car.position()).isEqualTo(0);
    }

}
