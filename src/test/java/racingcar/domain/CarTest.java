package racingcar.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;
import static racingcar.domain.CarFixture.벤츠;

@DisplayName("[테스트] 자동차")
public class CarTest {

    @Nested
    @DisplayName("[조건] 이름")
    class NameTest {

        @DisplayName("자동차 이름은 빈 문자열 또는 null 값을 가질 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void nullOrEmpty(final String name) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> new Car(name));
        }

        @DisplayName("자동차 이름은 5자 이내의 이름을 가진다")
        @ParameterizedTest
        @ValueSource(strings = { "A", "AB", "ABC", "ABCD", "ABCDE" })
        void oneToFiveLetters(final String name) {
            assertThatCode(() -> new Car(name))
                    .doesNotThrowAnyException();
        }

        @DisplayName("자동차 이름은 5자를 넘어갈 수 없다")
        @ParameterizedTest
        @ValueSource(strings = { "ABCDEF", "ABCDEFG", "ABCDEFGH" })
        void graterThanFiveLetters(final String name) {
            assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> new Car(name));
        }

    }

    @Nested
    @DisplayName("[조건] 이동")
    class MovingTest {

        @DisplayName("이동 조건이 4미만인 경우 움직이지 않는다")
        @ParameterizedTest
        @ValueSource(ints = { 0, 1, 2, 3 })
        void lessThanFour(final int condition) {
            var car = 벤츠;
            assertThat(car.getPosition()).isEqualTo(0);
        }

        @DisplayName("이동 조건이 4이상인 경우 움직인다")
        @ParameterizedTest
        @ValueSource(ints = { 4, 5, 6, 7, 8, 9 })
        void fourOrMore(final int condition) {
            var car = 벤츠;
            assertThat(car.getPosition()).isEqualTo(1);
        }

    }

}
