package racingcar;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

    @Test
    @DisplayName("자동차의 이름은 5자 이하여야 한다.")
    void nameCanOver5() {
        // when & then
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("123456"));
    }

    @Test
    @DisplayName("차가 전진할수 있는때 차의 포지션이 1증가한다.")
    void moveForward() {
        // given
        final Car car = new Car("car");
        // when
        car.move(new GoStrategy());
        // then
        Assertions.assertThat(car.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("차가 전진할수 없는때 차의 포지션은 변하지 않는다.")
    void stop() {
        // given
        final Car car = new Car("car");
        // when
        car.move(new StopStrategy());
        // then
        Assertions.assertThat(car.position()).isEqualTo(0);
    }

}
