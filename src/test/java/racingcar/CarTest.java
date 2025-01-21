package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {
    @DisplayName("자동차 이름은 5글자를 초과하면 에외가 발생한다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("숫자가 4 이상이면 자동차가 움직인다")
    @ParameterizedTest
    @ValueSource(ints = {4,9})
    void move(final int condition) {
        final var car = new Car("jason");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만이면 자동차는 정지한다")
    @ParameterizedTest
    @ValueSource(ints = {4,9})
    void stop(final int condition) {
        final var car = new Car("jason");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
