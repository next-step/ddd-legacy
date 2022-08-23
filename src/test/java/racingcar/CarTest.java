package racingcar;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 MovingStrategy에 따라 결정된다.
 */
class CarTest {

    @DisplayName("자동차 이름은 5글자 이하이다")
    @ParameterizedTest
    @ValueSource(strings = {"a", "ab", "abc", "abcd", "abcde"})
    void constructor(final String name) {
        assertThatCode(() -> new Car(name, new RandomMovingStrategy(new RandomNumber())))
                  .doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
    @Test
    void constructor_with_max_size_name() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Car("abcdef", new RandomMovingStrategy(new RandomNumber())));
    }

    @DisplayName("자동차 이름은 비어 있을 수 없다")
    @ParameterizedTest
    @NullAndEmptySource
    void constructor_with_empty_and_null_name(final String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new Car(name, new RandomMovingStrategy(new RandomNumber())));
    }

    @DisplayName("자동차는 움직일 수 있으면 움직인다.")
    @Test
    void move() {
        final Car car = new Car("iljun", new ForwardStrategy());
        final int previousPosition = car.currentPosition();
        car.move();
        Assertions.assertThat(car.currentPosition() == previousPosition + 1);
    }

    @DisplayName("자동차는 움직 일 수 없으면 움직이지 않는다.")
    @Test
    void hold() {
        final Car car = new Car("iljun", new HoldStrategy());
        final int previousPosition = car.currentPosition();
        car.move();
        Assertions.assertThat(car.currentPosition() == previousPosition);
    }
}
