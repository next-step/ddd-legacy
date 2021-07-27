package study;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void nameLength() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Car("123456"));
    }

    @DisplayName("자동차가 이동한다.")
    @Test
    void move() {
        final Car car = new Car("조준희");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 정지한다.")
    @Test
    void not_move() {
        final Car car = new Car("조준희");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }

}
