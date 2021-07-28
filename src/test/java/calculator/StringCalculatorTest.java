package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {
    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    @Test
    void nullOrEmpty() {
        assertThat(calculator.add(null)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void oneNumber() {
        assertThat(calculator.add("1")).isEqualTo(1);
    }

    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @Test
    void twoNumber() {
        assertThat(calculator.add("1,2")).isEqualTo(3);
    }

    @DisplayName("구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다.")
    @Test
    void colons() {
        assertThat(calculator.add("1,2:3")).isEqualTo(6);
    }

    @DisplayName("//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customDelimiter() {
        assertThat(calculator.add("//;\n1;2;3")).isEqualTo(6);
    }

    @Test
    void temp() {
        String[] split = "1".split(",");
        assertThat(split).isEqualTo(new String[]{"1"});
    }

    @Test
    void matcher() {
        String text = "//;\n1;2;3";
        Matcher matcher = Pattern.compile("//(.)\n(.*)").matcher(text);
        matcher.find();
        System.out.println(matcher.group(1));
        assertThat(matcher.group(1)).isEqualTo(";");
    }
}
