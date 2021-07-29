package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {

    @DisplayName("자동차 이름은 5자를 넘을 수 없다.")
    @Test
    void construct() {
        assertThatThrownBy(() -> {
                Car car = new Car("안녕하세요ㅎ");
        }).isInstanceOf(IllegalArgumentException.class).hasMessage("자동차 이름은 5자를 넘을 수 없습니다.");
    }

    @DisplayName("자동차가 이동한다.")
    @Test
    void move() {
        final Car car = new Car("제이슨");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직이지 않는다.")
    @Test
    void not_move() {
        final Car car = new Car("제이슨");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
