package racingcar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("[테스트] 자동차")
public class CarTest {

    @Nested
    @DisplayName("[조건] 이름")
    class NameTest {

        @DisplayName("자동차 이름은 빈 문자열 또는 null 값을 가질 수 없다")
        @ParameterizedTest
        @NullAndEmptySource
        void nullOrEmpty(final String name) {
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Car(name));
        }

        @DisplayName("자동차 이름은 5자 이내의 이름을 가진다")
        @ParameterizedTest
        @ValueSource(strings = {"A", "AB", "ABC", "ABCD", "ABCDE"})
        void oneToFiveLetters(final String name) {
            assertThatCode(() -> new Car(name)).doesNotThrowAnyException();
        }

        @DisplayName("자동차 이름은 5자를 넘어갈 수 없다")
        @ParameterizedTest
        @ValueSource(strings = {"ABCDEF", "ABCDEFG", "ABCDEFGH"})
        void graterThanFiveLetters(final String name) {
            assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new Car(name));
        }

    }

    @Nested
    @DisplayName("[조건] 이동")
    class MovingTest {

        private Car 벤츠;

        @BeforeEach
        void setUp() {
            벤츠 = new Car("벤츠");
        }

        @DisplayName("이동 조건이 충족되는 경우 움직일 수 있다")
        @Test
        void move() {
            벤츠.move(new ForwardStrategy());
            assertThat(벤츠.getPosition()).isEqualTo(1);
        }

        @DisplayName("이동 조건이 충족되지 않은 경우 움직일 수 없다")
        @Test
        void stop() {
            벤츠.move(new StopStrategy());
            assertThat(벤츠.getPosition()).isEqualTo(0);
        }

    }

}
