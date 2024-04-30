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
    @DisplayName("값이 4이상이 경우 Car는 앞으로 움직인다.")
    void moveForward() {
        // given
        final Car car = new Car("car");
        // when
        car.move(new GoStrategy());
        // then
        Assertions.assertThat(car.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("값이 4미만인 경우 Car는 정지한다.")
    void stop() {
        // given
        final Car car = new Car("car");
        // when
        car.move(new StopStrategy());
        // then
        Assertions.assertThat(car.position()).isEqualTo(0);
    }

}