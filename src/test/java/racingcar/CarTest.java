package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("자동차 테스트")
class CarTest {
    @DisplayName("자동차를 생성한다.")
    @Test
    void constructor() {
        assertThatCode(() -> new Car("자동차이름"))
                .doesNotThrowAnyException();
    }

    @DisplayName("자동차의 이름이 5글자가 넘는 경우, IllegalArgumentException이 발생한다.")
    @Test
    void constructorMoreThan5Letters() {
        assertThatThrownBy(() -> new Car("자동차이름름"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차의 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructorNullOrEmpty(String name) {
        assertThatThrownBy(() -> new Car(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        Car car = new Car("자동차", 0);
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 움직일 수 없는 경우 정지해있는다.")
    @Test
    void hold() {
        Car car = new Car("자동차", 0);
        car.move(() -> false);
        assertThat(car.getPosition()).isZero();
    }
}
