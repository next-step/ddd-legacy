package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @DisplayName("생성자 자동차 이름 검증 테스트.")
    @Test
    void carName() {
        assertThatThrownBy(() -> new Car("limdingdong"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void move() {
        Car carA = new Car("carA", 0);
        carA.move(new MustMoveStrategy());
        assertThat(carA.getPosition()).isOne();
    }

    @DisplayName("자동차가 움직이지 않는다.")
    @Test
    void stop() {
        Car carA = new Car("carA", 0);
        carA.move(new MustStopStrategy());
        assertThat(carA.getPosition()).isZero();
    }
}