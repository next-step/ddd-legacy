package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class CarTest {

    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructor() {
        assertThatCode(() -> new Car("car"))
            .doesNotThrowAnyException();

    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty_name(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor_illegal() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Car("동해물과 백두산이"));
    }

    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        final Car car = new Car("car", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }


    @DisplayName("자동차는 움직일 수 없는 경우 정지한다.")
    @Test
    void stop() {
        final Car car = new Car("car", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
