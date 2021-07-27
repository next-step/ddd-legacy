package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CarTest {
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructorCarNameMaxLengthOver5() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과 백두산이"));
    }

    @DisplayName("자동차가 이동한다.")
    @Test
    void move() {
        final Car car = new Car("loop");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 정지한다.")
    @Test
    void not_move() {
        final Car car = new Car("loop");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
