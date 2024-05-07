package calculator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberTest {

    @Nested
    class constructorTest {

        @DisplayName("양수가 들어오면 정상적으로 객체를 생성한다.")
        @Test
        void constructor() {
            assertThatCode(() -> new Number(1)).doesNotThrowAnyException();
        }

        @Nested
        class validateNegativeTest {
            @DisplayName("음수가 들어오면 RuntimeException을 던진다.")
            @Test
            void validateNegative() {
                assertThatThrownBy(() -> new Number(-1))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("음수는 입력할 수 없습니다.");
            }
        }
    }

    @Nested
    class addTest {
        @DisplayName("숫자를 더하는 테스트")
        @Test
        void add() {
            Number number = new Number(1);

            Number actual = number.add(new Number(2));

            assertThat(actual).isEqualTo(new Number(3));
        }
    }
}
