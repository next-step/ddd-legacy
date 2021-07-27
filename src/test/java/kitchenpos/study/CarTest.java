package kitchenpos.study;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void Validate_CarName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("carName"));
    }

    @DisplayName("자동차 이동")
    @Test
    void move_always() {
        final Car car = new Car("red");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차 이동")
    @Test
    void move_stop() {
        final Car car = new Car("red");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
