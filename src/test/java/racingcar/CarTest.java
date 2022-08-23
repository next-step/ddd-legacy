package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import racingcar.strategy.ForwardStrategy;
import racingcar.strategy.HoldStrategy;

import static org.assertj.core.api.Assertions.*;

/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class CarTest {

    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructor_with_name() {
        assertThatCode(() -> new Car("paul")).doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor_with_illegal_name() {
        assertThatThrownBy(() -> new Car("DDD 미션 달려보자")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void constructor_with_null_and_empty_name(final String name) {
        assertThatThrownBy(() -> new Car(name)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차 위치는 0보다 크거나 같아야한다.")
    @Test
    void constructor_with_name_and_position() {
        assertThatCode(() -> new Car("paul", 0)).doesNotThrowAnyException();
    }

    @DisplayName("자동차 위치는 음수일 수 없다.")
    @Test
    void constructor_with_negative_position() {
        assertThatThrownBy(() -> new Car("paul", -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직이는 조건이 해당하면 이동한다.")
    @Test
    void move() {
        final Car car = new Car("paul", 0);
        car.move(new ForwardStrategy());
        assertThat(car).isEqualTo(new Car("paul", 1));
    }

    @DisplayName("자동차가 움직이는 조건이 해당하지 않으면 정지한다.")
    @Test
    void stop() {
        final Car car = new Car("paul", 0);
        car.move(new HoldStrategy());
        assertThat(car).isEqualTo(new Car("paul", 0));
    }
}