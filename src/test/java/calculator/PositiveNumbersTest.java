package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class PositiveNumbersTest {

    @DisplayName("유효하지 않은 생성 파라미터를 전달하는 경우 예외를 발생시킨다")
    @ParameterizedTest
    @NullAndEmptySource
    void constructor(final List<PositiveNumber> values) {

        // when & then
        assertThatThrownBy(() -> new PositiveNumbers(values))
            .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("numbers를 모두 더한 값을 반환한다")
    @Test
    void sum() {
        // given
        final PositiveNumbers positiveNumbers = create(1, 2, 3);

        // when
        final int actual = positiveNumbers.sum();

        // then
        assertThat(actual == 6).isTrue();
    }

    private PositiveNumbers create(final int... numbers) {
        return new PositiveNumbers(Arrays.stream(numbers)
            .mapToObj(PositiveNumber::new)
            .collect(Collectors.toUnmodifiableList()));
    }
}
