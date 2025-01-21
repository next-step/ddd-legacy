package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CarTest {
    @DisplayName("자동차의 이름은 5글자를 초과하면 예외가 발생한다")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("자동차는 움직인다")
    @Test
    void move() {
        final var car = new Car("자동차");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        final var car = new Car("자동차");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
