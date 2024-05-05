package calculator2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class TextInputHandlerTest {

    private final TextInputHandler textInputHandler = new TextInputHandler();

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("문자열이 null이거나 비어있을 때, isBlank 메소드는 true를 리턴해야 한다")
    void whenStringIsNullOrEmpty_thenIsBlankReturnsTrue(String input) {
        assertThat(textInputHandler.isBlank(input)).isTrue();
        assertThat(textInputHandler.isBlank(input)).isTrue();
    }

    @Test
    @DisplayName("문자열이 null이거나 비어있지 않을 때, isBlank 메소드는 false를 리턴해야 한다")
    void whenStringIsNotNullOrEmpty_thenIsBlankReturnsFalse() {
        assertThat(textInputHandler.isBlank("text")).isFalse();
    }

    @Test
    @DisplayName("기본 구분자를 사용하여 텍스트를 토큰화한다")
    void tokenizeUsingDefaultDelimiters() {
        String text = "token1,token2:token3";
        String[] tokens = textInputHandler.tokenize(text);
        assertThat(tokens).containsExactly("token1", "token2", "token3");
    }

    @Test
    @DisplayName("사용자 정의 구분자를 사용하여 텍스트를 토큰화한다")
    void tokenizeUsingCustomDelimiter() {
        String text = "//;\ntoken1;token2;token3";
        String[] tokens = textInputHandler.tokenize(text);
        assertThat(tokens).containsExactly("token1", "token2", "token3");
    }

    @Test
    @DisplayName("토큰화 인자가 null인 경우")
    void whenTokenizeArgumentIsNull_thenThrowsNullPointerException() {
        assertThatThrownBy(() -> textInputHandler.tokenize(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("null");
    }
}