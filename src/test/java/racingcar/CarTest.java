package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {
    @DisplayName("자동차의 이름은 5글자를 초과하면 예외가 발생한다.")
    @Test
    void constructor() {
     assertThatIllegalArgumentException().isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("숫자가 4 이상이면 자동차는 움직인다.")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @ParameterizedTest
    void move(final int condition){
        final var car = new Car("jason");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만이면 자동차는 정지한다.")
    @ValueSource(ints = {1, 2, 3})
    void stop(final int condition){
        final var car = new Car("jason");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(0);
    }
}