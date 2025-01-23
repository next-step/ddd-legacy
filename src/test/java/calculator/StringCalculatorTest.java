package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

class StringCalculatorTest {

    private StringCalculator stringCalculator;


    @BeforeEach
    void setup() {
        DelimiterGroup delimiterGroup = new DelimiterGroup();
        NumberExtractor numberExtractor = new NumberExtractor();
        stringCalculator = new StringCalculator(delimiterGroup, numberExtractor);
    }

    @Test
    @DisplayName("입력된 문자열의 합을 계산한다.")
    void testAdd() {
        // given
        final String text = "1,2";

        // when
        final int result = stringCalculator.add(text);

        // then
        assertThat(result).isEqualTo(3);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("입력이 null 또는 빈 문자열일 경우 0을 반환한다.")
    void testReturn0WhenEmptyText(final String text) {
        // when
        final int result = stringCalculator.add(text);

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("기본 구분자는 `,`, `:`를 사용한다.")
    void testCheckDelimiter() {
        // given
        final String text = "1,2:3";

        // when
        final int result = stringCalculator.add(text);

        // then
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("`//`와 `\\n` 사이의 문자를 커스텀 구분자로 사용한다.")
    void testCustomDelimiter() {
        // given
        final String text = "//;\\n1;2;3";

        // when
        final int result = stringCalculator.add(text);

        // then
        assertThat(result).isEqualTo(6);
    }

    @Test
    @DisplayName("음수를 전달하면 RuntimeException이 발생한다.")
    void testNegativeNumber() {
        // given
        final String text = "1;2;-3";

        // when & then
        assertThatException()
                .isThrownBy(() -> stringCalculator.add(text))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("숫자 이외의 값을 전달하면 RuntimeException이 발생한다.")
    void testNotNumericValue() {
        // given
        final String text = "1;a;3";

        // when & then
        assertThatException()
                .isThrownBy(() -> stringCalculator.add(text))
                .isInstanceOf(RuntimeException.class);
    }

}
