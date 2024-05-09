package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StringValidationTest {

    @DisplayName(value = "빈 문자열인 경우 0 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull2(final String text){
        StringValidation stringValidation = new StringValidation(text);
        assertThat(stringValidation.getText()).isEqualTo("0");
    }

    @DisplayName(value = "음수 전달 시 RuntimeException 예외 처리")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negative2(final String text){
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new StringValidation(text));
    }


}
