package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import racingcar.strategy.ForwardMovingStrategy;
import racingcar.strategy.StopMovingStrategy;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("자동차 테스트")
public class CarTest {

    @DisplayName("자동차 이름은 5글자를 초과할 수 없다")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("123456"));
    }

    @DisplayName("자동차는 전진 가능한 조건이면 한 칸 움직일 수 있다.")
    @Test
    void move() {
        final Car car = new Car("woozi");
        car.move(new ForwardMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 전진 불가능한 조건이면 움직이지 않는다.")
    @Test
    void stop() {
        final Car car = new Car("woozi");
        car.move(new StopMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
