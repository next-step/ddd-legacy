package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CarTest {

    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void carName() {
        final var actual = new Car("pobi");
        assertThat(actual.getName()).isEqualTo("pobi");
    }

    @DisplayName("자동차 이름은 5자 이하만 가능하다.")
    @Test
    void carNameLength() {
        final var actual = new Car("pobii");
        assertThat(actual.getName()).isEqualTo("pobii");
    }

    @DisplayName("자동차 이름은 5자 이상이면 IllegalArgumentException이 발생한다.")
    @Test
    void carNameLengthException() {
        assertThatThrownBy(() -> new Car("pobiiii"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("자동차 이름은 5자 이하만 가능합니다.");
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다.")
    @Test
    void carMove() {
        final var car = new Car("pobi");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 4 미만인 경우 정지한다.")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void carStop(final int condition) {
        final var car = new Car("pobi");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void carStop() {
        final var car = new Car("pobi");
        car.move(() -> false);
        assertThat(car.getPosition()).isZero();
    }

}
