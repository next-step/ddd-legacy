package kitchenpos.stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TextCalculateTypeTest {

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 NullOrEmpty을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(TextCalculateType.of(text)).isEqualTo(TextCalculateType.NullOrEmpty);
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 SingleNumber을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(TextCalculateType.of(text)).isEqualTo(TextCalculateType.SingleNumber);
    }

    @DisplayName(value = "구분자를 쉼표(,) 콜론(:)을 사용할 경우 CommaAndColon을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(TextCalculateType.of(text)).isEqualTo(TextCalculateType.CommaAndColon);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 경우 CustomDelimiter을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(TextCalculateType.of(text)).isEqualTo(TextCalculateType.CustomDelimiter);
    }

    @DisplayName(value = "지원하지 않는 형식의 문자의 경우 NotFound를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/**;\n1(2)3"})
    void notFound(final String text) {
        assertThat(TextCalculateType.of(text)).isEqualTo(TextCalculateType.NotFound);
    }
}
