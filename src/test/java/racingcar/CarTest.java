package racingcar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CarTest {

    @DisplayName("자동차는 이름을 가지고 있다.")
    @Test
    public void name() throws Exception {
        final var car = new Car("car");

        assertThat(car.getName()).isEqualTo("car");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다.")
    @Test
    public void invalidName() throws Exception {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상일 경우 전진한다.")
    @Test
    public void move() throws Exception {
        Car car = new Car("car");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하일 경우 정지한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    public void stop(int condition) throws Exception {
        Car car = new Car("car");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다.")
    @Test
    public void stop() throws Exception {
        Car car = new Car("car");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }
}
