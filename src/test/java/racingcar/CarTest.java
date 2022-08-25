package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;
import static racingcar.Car.*;

public class CarTest {

    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructor() {
        assertThatNoException().isThrownBy(() -> of("람보르기니"));
    }

    @DisplayName("자동차 이름이 5글자를 넘을 경우 예외 발생 테스트")
    @Test
    void validCarNameSize() {
        assertThatThrownBy(() -> of("람보르기니우라칸")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty_name(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> of(name));
    }


    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        Car car = of("람보르기니");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 움직일 수 없는 경우 이동하지 않는다.")
    @Test
    void stop() {
        Car car = of("람보르기니");
        assertThat(car.getPosition()).isZero();
    }

}
