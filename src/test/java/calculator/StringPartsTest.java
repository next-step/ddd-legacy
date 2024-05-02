package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

public class StringPartsTest {

    @DisplayName("문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "123"})
    void constructor(String input) {
        StringParts stringParts = new StringParts(input);
        assertThat(stringParts.parts()).containsExactly(input);
    }

    @DisplayName("쉼표 또는 콜론을 구분자로 가지는 문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1:2:3"})
    void delimiter_constructor(String input) {
        StringParts stringParts = new StringParts(input);
        assertThat(stringParts.parts()).containsExactly("1", "2", "3");
    }

    @DisplayName("커스텀 구분자(//{delimiter}\n)를 가지는 문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"//-\n1-2-3", "//a\n1a2a3"})
    void custom_delimiter_constructor(String input) {
        StringParts stringParts = new StringParts(input);
        assertThat(stringParts.parts()).containsExactly("1", "2", "3");
    }

    @DisplayName("음수, 숫자 이외의 값이 포함되면 문자열 부품을 생성을 실패한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,-2,3", "l,2,3"})
    void constructor_fail(String input) {
        assertThatRuntimeException()
                .isThrownBy(() -> new StringParts(input));
    }
}
