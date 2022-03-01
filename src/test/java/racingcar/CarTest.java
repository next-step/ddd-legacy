package racingcar;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다.")
    @Test
    void forward() {
        final Car car = new Car("woody", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }


    @DisplayName("자동차가 움직이지 않는다.")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void hold(final int number) {
        final Car car = new Car("woody", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }
}
