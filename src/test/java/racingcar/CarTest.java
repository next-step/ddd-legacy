package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import racingcar.domain.Car;
import racingcar.domain.MoveStrategy;
import racingcar.domain.RandomStrategy;

import java.util.Random;

@DisplayName("자동차 도메인 테스트")
public class CarTest {
    MoveStrategy moveStrategy;
    Car car;

    @BeforeEach
    void setUp() {
        moveStrategy = new RandomStrategy();
        car = new Car();
    }

    @Test
    @DisplayName("자동차의 이름은 5글자를 넘을 수 없다.")
    void handleNameLength() {
        String name = "abcdef";
        Assertions.assertThatThrownBy(
                () -> new Car(name)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("값이 4 이상일 경우 자동차가 움직인다.")
    void move() {
        int randomValue = 4;

        Assertions.assertThat(car.movable(moveStrategy, randomValue))
                .isEqualTo(true);
    }

    @Test
    @DisplayName("값이 4 미만일 경우 자동차는 움직이지 않는다.")
    void stop() {
        int randomValue = 3;

        Assertions.assertThat(car.movable(moveStrategy, randomValue))
                .isEqualTo(false);
    }

    @Test
    @DisplayName("랜덤 값에 따른 자동차 움직임 결과를 확인한다.")
    void movableByRandomValue() {
        int randomValue = new Random().nextInt(10);
        System.out.println("랜덤 값 : " + randomValue);
        boolean result = car.movable(moveStrategy, randomValue);
        System.out.println("결과 : " + result);
    }
}