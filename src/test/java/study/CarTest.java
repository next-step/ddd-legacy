package study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import study.moving.GoStrategy;
import study.moving.StopStrategy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CarTest {
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void nameLength() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("자동차가 이동한다.")
    @Test
    void move() {
        Car car = new Car("dave");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 정지한다.")
    @Test
    void not_move() {
        Car car = new Car("dave");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
