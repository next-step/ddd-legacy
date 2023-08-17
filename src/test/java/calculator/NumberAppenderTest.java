package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberAppenderTest {

    @Test
    @DisplayName("정해진 구분 자를 만날 경우 Builder에 쌓은 스트링을 넘버 스트링으로 옮김")
    void ifDelimiter() {
        // given
        NumberAppender numberAppender = new NumberAppender(new TargetString(""));
        NumberStrings initNumberStrings = numberAppender.getNumberStrings();

        assertThat(initNumberStrings.getNumbers()).isEmpty();

        // when
        numberAppender.appendToNumberStrings('1');
        numberAppender.appendToNumberStrings(',');
        numberAppender.appendToNumberStrings('5');
        numberAppender.appendToNumberStrings(':');

        // then
        NumberStrings actual = numberAppender.getNumberStrings();
        assertThat(actual.getNumbers()).hasSize(2);
        assertThat(actual.getNumbers()).contains("1", "5");
    }

    @Test
    @DisplayName("정해진 구분 자가 아닌 값 입력시 예외 발생")
    void notAllowed() {
        NumberAppender numberAppender = new NumberAppender(new TargetString("123@22"));
        char notAllowed = '@';
        assertThatThrownBy(() -> numberAppender.appendToNumberStrings(notAllowed))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage(notAllowed + "는 허용된 구분자가 아니거나 숫자가 아닙니다.");
    }
}