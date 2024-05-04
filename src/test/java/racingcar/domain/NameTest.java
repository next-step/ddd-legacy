package racingcar.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NameTest {

    @DisplayName("생성자 테스트")
    @Nested
    class constructorTest {
        @DisplayName("이름이 5자 이하인 경우에 생성된다.")
        @Test
        void normalConstructorTest() {
            assertThatCode(() -> new Name("12345")).doesNotThrowAnyException();
        }

        @DisplayName("이름이 5자 이상인 경우에 에러가 발생한다.")
        @Test
        void validateNameOfMoreThanFiveLengthExceptionTest() {
            assertThatThrownBy(() -> new Name("123456"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 5자 이하여야 합니다.");
        }

        @DisplayName("이름이 null이거나 빈값이면 에러가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void validateNameOfNullAndEmptyExceptionTest(String value) {
            assertThatThrownBy(() -> new Name(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이름은 빈 문자열이 될 수 없습니다.");
        }
    }
}
