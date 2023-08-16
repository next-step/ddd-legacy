package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    @DisplayName("빈 문자열의 경우 0을 반환")
    @Test
    void emptyValueTest() {
        Separator separator = new Separator();
        TargetString targetString = new TargetString("");

        NumberStrings result = separator.separate(targetString);

        assertThat(result.isEmpty()).isTrue();
        assertThat(Calculator.add(result)).isEqualTo(0);
    }

    @DisplayName("숫자 한자리도 정상 반환한다.")
    @Test
    void singleValueTest() {
        Separator separator = new Separator();
        TargetString targetString = new TargetString("5");

        NumberStrings result = separator.separate(targetString);

        assertThat(result.getNumbers()).contains("5");
        assertThat(Calculator.add(result)).isEqualTo(5);
    }

    @DisplayName("쉼표 구분자를 처리하여 1,2 -> 3")
    @Test
    void addDelimitersTest1() {
        Separator separator = new Separator();
        TargetString targetString = new TargetString("11:23");

        NumberStrings result = separator.separate(targetString);

        assertThat(result.getNumbers()).containsExactly("11", "23");
        assertThat(Calculator.add(result)).isEqualTo(34);
    }

    @DisplayName("콜론 구분자를 처리하여 3:4 -> 7")
    @Test
    void addDelimitersTest2() {
        Separator separator = new Separator();
        TargetString targetString = new TargetString("3:4");

        NumberStrings result = separator.separate(targetString);

        assertThat(result.getNumbers()).containsExactly("3", "4");
        assertThat(Calculator.add(result)).isEqualTo(7);
    }

    @DisplayName("두 자리, 세 자리 값도 계산 가능하다.")
    @Test
    void addBigNum() {
        Separator separator = new Separator();
        TargetString targetString = new TargetString("11:23:134");

        NumberStrings result = separator.separate(targetString);

        assertThat(result.getNumbers()).containsExactly("11", "23", "134");
        assertThat(Calculator.add(result)).isEqualTo(168);
    }

    @DisplayName("정해진 구분자 혹은 숫자가 아니면 Runtime 예외 발생")
    @Test
    void runtimeExceptionTest1() {
        Separator separator = new Separator();

        TargetString targetString1 = new TargetString("11!23");
        TargetString targetString2 = new TargetString("안녕,23");

        assertThatThrownBy(() -> separator.separate(targetString1)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> separator.separate(targetString2)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("음수의 경우에 Runtime 예외 발생")
    @Test
    void runtimeExceptionTest2() {
        Separator separator = new Separator();

        TargetString targetString = new TargetString("-1,23");

        assertThatThrownBy(() -> separator.separate(targetString)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customDelimiter() {
        Separator separator = new Separator();
        TargetString targetString = new TargetString("//!\n131!313");

        NumberStrings result = separator.separate(targetString);

        assertThat(result.getNumbers()).containsExactly("131", "313");
        assertThat(Calculator.add(result)).isEqualTo(444);
    }
}

