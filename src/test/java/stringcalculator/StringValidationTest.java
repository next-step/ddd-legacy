package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StringValidationTest {

    private StringValidation stringValidation;

    @BeforeEach
    public void setUp() {
        stringValidation = new StringValidation();
    }

    @DisplayName(value = "빈 문자열일 경우 TRUE 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull1(final String text) {
        assertThat(stringValidation.isNullOrEmpty(text)).isTrue();
    }

    @DisplayName(value = "음수를 전달하는 경우 True 반환")
    @Test
    void negative1(){
        assertThat(stringValidation.checkNegative("-1")).isTrue();
    }

    @DisplayName(value = "parseNumber 함수 : 빈 문자열인 경우 0 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull2(final String text){
        assertThat(stringValidation.parseNumber(text)).isZero();
    }

    @DisplayName(value = "parseNumber 함수 : 음수 전달 시 RuntimeException 예외 처리")
    @Test
    void negative2(){
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> stringValidation.parseNumber("-1"));
    }


}
