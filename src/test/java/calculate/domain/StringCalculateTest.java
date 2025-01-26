package calculate.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculateTest {

    @ParameterizedTest
    @ValueSource(strings = {"1,2,3"})
    void 문자열_덧셈_계산기_생성_성공_테스트(final String text) {
        final StringCalculate calculate = new StringCalculate(text);

        assertThat(calculate.sum()).isEqualTo(6);
    }

    @Test
    void 문자열_덧셈_계산기_null_입력인_경우_성공_테스트() {
        StringCalculate calculate = new StringCalculate((String) null);

        assertThat(calculate.getNumbers()).isEqualTo(new Numbers(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void 구분자를_쉼표_이외에_콜론을_사용할_수_있다(final String text) {
        StringCalculate calculate = new StringCalculate(text);

        assertThat(calculate.sum()).isEqualTo(6);
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void 문자열_커스텀_구분자_숫자_3개_성공_테스트(final String text) {
        StringCalculate calculate = new StringCalculate(text);

        assertThat(calculate.sum()).isEqualTo(6);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void 숫자_두개를_쉼표_구분자로_입력할_경우_두_숫자의_합을_반환한다(final String text) {
        StringCalculate calculate = new StringCalculate(text);

        assertThat(calculate.sum()).isEqualTo(3);
    }

}