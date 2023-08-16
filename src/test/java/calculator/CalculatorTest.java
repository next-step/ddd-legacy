package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    @DisplayName("빈 문자열의 경우 0을 반환")
    @Test
    void emptyValueTest() {
        Separator separator = new Separator();
        List<String> separate = separator.separate("");
        assertThat(separate).isEmpty();
        assertThat(Calculator.add(separate)).isEqualTo(0);
    }

    @DisplayName("쉼표 구분자를 처리하여 1,2 -> 3")
    @Test
    void addDelimitersTest1() {
        Separator separator = new Separator();
        List<String> separate = separator.separate("11:23");
        assertThat(separate).containsExactly("11", "23");
        assertThat(Calculator.add(separate)).isEqualTo(34);
    }

    @DisplayName("콜론 구분자를 처리하여 3:4 -> 7")
    @Test
    void addDelimitersTest2() {
        Separator separator = new Separator();
        List<String> separate = separator.separate("3:4");
        assertThat(separate).containsExactly("3", "4");
        assertThat(Calculator.add(separate)).isEqualTo(7);
    }

    @DisplayName("두 자리, 세 자리 값도 계산 가능하다.")
    @Test
    void addBigNum() {
        Separator separator = new Separator();
        List<String> separate = separator.separate("11:23:134");
        assertThat(separate).containsExactly("11", "23", "134");
        assertThat(Calculator.add(separate)).isEqualTo(168);
    }

    @DisplayName("정해진 구분자 혹은 숫자가 아니면 Runtime 예외 발생")
    @Test
    void runtimeExceptionTest1() {
        Separator separator = new Separator();
        assertThatThrownBy(() -> separator.separate("11!23")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> separator.separate("안녕,23")).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("음수의 경우에 Runtime 예외 발생")
    @Test
    void runtimeExceptionTest2() {
        Separator separator = new Separator();
        assertThatThrownBy(() -> separator.separate("-1,23")).isInstanceOf(RuntimeException.class);
    }
}

