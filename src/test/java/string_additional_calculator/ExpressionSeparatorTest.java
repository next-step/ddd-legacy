package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExpressionSeparator 클래스")
class ExpressionSeparatorTest {

    private final ExpressionSeparator expressionSeparator = new ExpressionSeparator();

    @DisplayName("기본 구분자를 사용해 식에서 상수들을 분리할 수 있다.")
    @Test
    void useDefaultSeparate() {
        // given
        final String expression = "1,2:3";

        // when
        String[] separate = expressionSeparator.separate(expression);

        // then
        assertThat(separate).containsExactly("1", "2", "3");
    }

    @DisplayName("커스텀 구분자를 사용해 식에서 상수들을 분리할 수 있다.")
    @Test
    void useCustomSeparate() {
        // given
        final String customSeparateString = "//;\n";
        final String expression = "1;2;3";

        // when
        String[] separate = expressionSeparator.separate(customSeparateString + expression);

        // then
        assertThat(separate).containsExactly("1", "2", "3");
    }

    @DisplayName("커스텀 구분자는 1글자를 초과할 수 없다.")
    @Test
    void invalidCustomSeparate() {
        // given
        final String customSeparateString = "//;;\n";
        final String expression = "1;;2;;3";

        // when then
        assertThatThrownBy(() -> expressionSeparator.separate(customSeparateString + expression))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("커스텀 구분자는 1글자여야 합니다. separator: ;;");
    }

    @DisplayName("커스텀 구분자는 빈 문자일 수 없다")
    @Test
    void emptyCustomSeparate() {
        // given
        final String customSeparateString = "//\n";
        final String expression = "1;;2;;3";

        // when then
        assertThatThrownBy(() -> expressionSeparator.separate(customSeparateString + expression))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("커스텀 구분자는 1글자여야 합니다. separator: ");
    }
}
