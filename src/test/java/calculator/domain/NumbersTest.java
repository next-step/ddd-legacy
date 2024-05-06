package calculator.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumbersTest {

    @Nested
    class constructorTest {
        @DisplayName("정상적으로 생성하는 테스트")
        @Test
        void normalOfTest() {
            String[] input = {"1", "2", "3"};
            assertThatCode(() -> new Numbers(input)).doesNotThrowAnyException();
        }

        @Nested
        class parseNumber {
            @DisplayName("숫자가 아닌 값이 들어오면 예외를 던지는 테스트")
            @Test
            void parseNumberExceptionTest() {
                String[] input = {"a", "2", "3"};
                assertThatThrownBy(() -> new Numbers(input))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("숫자가 아닌 값이 포함되어 있습니다.");
            }
        }
    }

    @Nested
    class sumTest {
        @DisplayName("빈값이 들어오면 0을 리턴한다.")
        @Test
        void sumTestByBlank() {
            String[] input = {};
            Numbers numbers = new Numbers(input);
            assertThat(numbers.sum()).isZero();
        }

        @DisplayName("모든 숫자의 합을 구할 수 있다.")
        @Test
        void normalSumTest() {
            String[] input = {"1", "2", "3"};
            Numbers numbers = new Numbers(input);
            assertThat(numbers.sum()).isEqualTo(6);
        }

        @DisplayName("숫자 하나만 들어온다면 해당 숫자를 반환한다.")
        @Test
        void sumTestByOneNumber() {
            String[] input = {"1"};
            Numbers numbers = new Numbers(input);
            assertThat(numbers.sum()).isEqualTo(1);
        }
    }
}
