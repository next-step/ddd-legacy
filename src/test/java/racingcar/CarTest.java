package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {
    @DisplayName("자동차 이름은 5글자를 초과하면 에외가 발생한다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("무조건 자동차가 움직인다")
    @Test
    void move() {
        final var car = new Car("jason");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("무조건 자동차가 멈춘다")
    @Test
    void stop() {
        final var car = new Car("jason");
        car.move(new StopStartegy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
