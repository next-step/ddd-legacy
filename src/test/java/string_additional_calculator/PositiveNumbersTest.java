package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PositiveNumbers 클래스")
class PositiveNumbersTest {
    @DisplayName("PositiveNumbers의 of 정적 팩토리 메서드는 숫자로된 문자열을 전달하면 PositiveNumbers 객체를 생성한다.")
    @Test
    void positiveNumbers() {
        // given
        List<String> numbers = List.of("1", "2", "3");

        // when
        final PositiveNumbers positiveNumbers = PositiveNumbers.of(numbers);

        // then
        assertThat(positiveNumbers.getValues()).extracting("value").containsExactly(1, 2, 3);
    }

    @DisplayName("PositiveNumbers의 of 정적 팩토리 메서드는 숫자가 아닌 문자열을 전달하면 RuntimeException을 던진다.")
    @Test
    void invalidPositiveNumbers() {
        // given
        List<String> numbers = List.of("일", "2", "3");

        // when
        assertThatThrownBy(() -> PositiveNumbers.of(numbers))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: 일");
    }

    @DisplayName("totalSum 메서드는 모든 값을 더한 값을 반환한다.")
    @Test
    void totalSum() {
        // given
        List<String> numbers = List.of("1", "2", "3");
        final PositiveNumbers positiveNumbers = PositiveNumbers.of(numbers);

        // when
        final PositiveNumber totalSum = positiveNumbers.totalSum();

        // then
        assertThat(totalSum).isEqualTo(PositiveNumber.from("6"));
    }
}
