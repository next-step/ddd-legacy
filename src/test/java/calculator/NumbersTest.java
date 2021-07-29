package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class NumbersTest {
    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 가진 리스트를 반환한다.")
    @Test
    void oneNumber() {
        assertThat(Numbers.of("1").toIntList())
                .isEqualTo(Arrays.asList(1));
    }

    @DisplayName("숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 리스트를 반환한다.")
    @Test
    void twoNumbers() {
        assertThat(Numbers.of("1,2").toIntList())
                .isEqualTo(Arrays.asList(1, 2));
    }

    @DisplayName("구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @Test
    void colons() {
        assertThat(Numbers.of("1,2:3").toIntList())
                .isEqualTo(Arrays.asList(1, 2, 3));
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customDelimiter() {
        assertThat(Numbers.of("//;\n1;2;3").toIntList())
                .isEqualTo(Arrays.asList(1, 2, 3));
    }
}
