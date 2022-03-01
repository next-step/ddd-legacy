package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {


    @DisplayName("자동차의 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("오늘은삼일절~~"))
                .isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("자동차가 전진한다.")
    @Test
    void go() {
        Car car = new Car("seok2");
        car.move(new AlwaysMovableStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직이지 않는다.")
    @Test
    void stop() {
        Car car = new Car("seok2");
        car.move(new NeverMovableStrategy());
        assertThat(car.getPosition()).isZero();
    }


}
