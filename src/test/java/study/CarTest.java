package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.constant.Constant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * [요구사항]
 * 1) 자동차 이름은 5 글자를 넘을 수 없다.
 * 2) 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 3) 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {

    @DisplayName("자동차 이름 길이 초과여부 테스트")
    @Test
    void carNameLength() {
        assertThrows(IllegalArgumentException.class, () -> {
            Car car = new Car("파란색자동차");
        });
    }

    @DisplayName("입력 값이 4 이상인 경우 자동차 이동 테스트")
    @Test
    void move() {
        Car car = new Car("CAR");
        car.move(Constant.CAR_MOVABLE_CONDITION_MIN);
        assertEquals(car.getPosition(), 1);
    }

    @DisplayName("입력 값이 4 미만인 경우 자동차 이동 테스트")
    @Test
    void notMove() {
        Car car = new Car("CAR");
        car.move(Constant.CAR_MOVABLE_CONDITION_MIN - 1);
        assertEquals(car.getPosition(), 0);
    }

    @DisplayName("확정적인 이동 조건 주입시 자동차 이동 테스트")
    @Test
    void validMove() {
        Car car = new Car("CAR");
        car.move(new ValidMovingStrategy());
        assertEquals(car.getPosition(), 1);
    }
}