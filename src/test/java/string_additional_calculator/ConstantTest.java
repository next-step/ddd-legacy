package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("Constant 클래스")
class ConstantTest {

    @DisplayName("Constant의 from 정적 팩토리 메서드는 숫자로된 문자열을 전달하면 숫자 값을 가진 객체를 생성한다.")
    @Test
    void constant() {
        // when
        final Constant constant = Constant.from("1");

        // then
        assertThat(constant.getValue()).isEqualTo(1);
    }

    @DisplayName("문자열 계산기의 상수를 숫자 이외의 값으로 생성하려고 하면 RuntimeException을 던진다.")
    @ParameterizedTest
    @CsvSource({
            "r,문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: r",
            "@,문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: @",
            "],문자열 계산기에 상수는 숫자 이외의 값은 전달할 수 없습니다. number: ]",
    })
    void invalidValue(String value, String exceptionMessage) {
        // when then
        assertThatThrownBy(() -> Constant.from(value))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(exceptionMessage);
    }

    @DisplayName("문자열 계산기의 상수를 음수로 생성하려고 하면 RuntimeException을 던진다.")
    @ParameterizedTest
    @CsvSource({
            "-1,문자열 계산기에 상수는 음수가 될 수 없습니다. number: -1",
            "-2,문자열 계산기에 상수는 음수가 될 수 없습니다. number: -2",
            "-100,문자열 계산기에 상수는 음수가 될 수 없습니다. number: -100",
    })
    void invalidNegativeNumber(String value, String exceptionMessage) {
        // when then
        assertThatThrownBy(() -> Constant.from(value))
                .isInstanceOf(RuntimeException.class)
                .hasMessage(exceptionMessage);
    }
}
