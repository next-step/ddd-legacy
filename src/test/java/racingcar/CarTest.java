package racingcar;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {
    @DisplayName("자동차의 이름은 5글자를 초과할 수 없다.")
    @Test
    void constructor() {
        // given
        String carName = "123456";

        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> new Car(carName);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(throwingCallable);
    }

    @DisplayName("숫자가 4 이상이면 자동차는 움직인다")
    @ParameterizedTest
    @ValueSource(ints = {4, 9})
    void move(final int condition) {
        // given
        Car car = new Car("붕붕");

        // when
        car.move(condition);

        // then
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만이면 자동차는 정지한다")
    @ParameterizedTest
    @ValueSource(ints = {0, 3})
    void notMove(final int condition) {
        // given
        Car car = new Car("붕붕");

        // when
        car.move(condition);

        // then
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
