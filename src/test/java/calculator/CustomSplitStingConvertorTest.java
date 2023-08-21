package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CustomSplitStingConvertorTest {

    private CustomSplitStingConvertor customSplitStingConvertor;

    @BeforeEach
    void setUp() {
        customSplitStingConvertor = new CustomSplitStingConvertor();
    }

    @DisplayName("'//'와 '\n' 문자 사이에 커스텀 구분자를 지원해야한다.")
    @Test
    void 지원정책확인() {
        assertThat(customSplitStingConvertor.isSupport("//;\n1;2;3")).isTrue();
        assertThat(customSplitStingConvertor.isSupport("//-\n1-2-3")).isTrue();
    }

    @DisplayName("양수가 아닐경우 에러를 리턴한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;-2;3", "//-\n1-2-가"})
    void 양수가아닌값에러(final String input) {
        assertThatThrownBy(() -> customSplitStingConvertor.calculate(input))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("'//;\n1;2;3'을 변환한 경우 1,2,3을 리턴해야한다.")
    @Test
    void 결과확인() {
        assertThat(customSplitStingConvertor.calculate("//;\n1;2;3").unmodifiableNumbers())
                .hasSize(3)
                .extracting(number -> number.value())
                .containsExactly(1,2,3);
    }

}