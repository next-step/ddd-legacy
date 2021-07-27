package kitchenpos.stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class TextCalculateTypesTest {

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 NullOrEmpty을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(TextCalculateTypes.of(text)).isEqualTo(TextCalculateTypes.NullOrEmpty);
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 SingleNumber을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(TextCalculateTypes.of(text)).isEqualTo(TextCalculateTypes.SingleNumber);
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 SingleComma을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(TextCalculateTypes.of(text)).isEqualTo(TextCalculateTypes.SingleComma);
    }

    @DisplayName(value = "구분자를 쉼표(,) 콜론(:)을 사용할 경우 CommaAndColon을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(TextCalculateTypes.of(text)).isEqualTo(TextCalculateTypes.CommaAndColon);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 경우 CustomDelimiter을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(TextCalculateTypes.of(text)).isEqualTo(TextCalculateTypes.CustomDelimiter);
    }

    @DisplayName(value = "지원하지 않는 형식의 문자의 경우 NotFound를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/**;\n1(2)3"})
    void notFound(final String text) {
        assertThat(TextCalculateTypes.of(text)).isEqualTo(TextCalculateTypes.NotFound);
    }

    @DisplayName(value = "NullOrEmpty인 경우 계산시 0을 반환 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull_value(final String text) {
        assertThat(TextCalculateTypes.NullOrEmpty.calculate(text)).isEqualTo(0);
    }

    @DisplayName(value = "SingleNumber인 경우 해당 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber_value(final String text) {
        assertThat(TextCalculateTypes.SingleNumber.calculate(text)).isEqualTo(1);
    }

    @DisplayName(value = "SingleComma인 경우의 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers_value(final String text) {
        assertThat(TextCalculateTypes.SingleComma.calculate(text)).isEqualTo(3);
    }

    @DisplayName(value = "CommaAndColon인 경우의 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons_value(final String text) {
        assertThat(TextCalculateTypes.CommaAndColon.calculate(text)).isEqualTo(6);
    }

    @DisplayName(value = "CustomDelimiter인 경우의 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter_value(final String text) {
        assertThat(TextCalculateTypes.CustomDelimiter.calculate(text)).isEqualTo(6);
    }

    @DisplayName(value = "NotFound인 경우 IllegalArgumentException을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/**;\n1(2)3"})
    void notFound_value(final String text) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> TextCalculateTypes.NotFound.calculate(text));
    }
}