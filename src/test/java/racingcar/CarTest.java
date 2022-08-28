package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void illegal_name_length() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("123456"));
    }

    @DisplayName("자동차 이름은 5자 이내이다.")
    @Test
    void constructor() {
        final Car car = new Car("12345");

        assertThat(car.getName()).isEqualTo("12345");
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void carNameShouldNotBeNullOrBlank(String name) {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car(name));
    }

    @DisplayName("움직일 수 있는 경우 1칸 움직인다.")
    @Test
    void movable() {
        final Car car = new Car("무빙", 0);
        car.move(new ForwardStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("움직일 수 없는 경우 멈춘다.")
    @Test
    void stop() {
        final Car car = new Car("스톱", 0);
        car.move(new HoldStrategy());

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
