package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;


class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp(){
        stringCalculator = new StringCalculator();
    }


    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우, 0을 반환해야한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text){
        assertThat(stringCalculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우, 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "5245234", "75234"})
    void oneNumber(final String text){
        assertThat(stringCalculator.add(text)).isEqualTo(Integer.parseInt(text));
    }


    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우, 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,3"})
    void splitComma(final String text) {
        assertThat(stringCalculator.add(text)).isEqualTo(4);
    }


    @DisplayName(value = "구분자를 컴마(,) 이외의 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,3:4"})
    void splitCommaColons(final String text) {
        assertThat(stringCalculator.add(text)).isEqualTo(8);
    }


    @DisplayName(value = "// 와 \n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(6);
    }

}
