package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ZeroStingConvertorTest {

    private ZeroStingConvertor zeroStingConvertor;

    @BeforeEach
    void setUp() {
        zeroStingConvertor = new ZeroStingConvertor();
    }

    @DisplayName("빈문자열, null을 지원해야한다.")
    @Test
    void 지원정책확인() {
        assertThat(zeroStingConvertor.isSupport(null)).isTrue();
        assertThat(zeroStingConvertor.isSupport("")).isTrue();
    }

    @DisplayName("null을 변환한 경우 0을 리턴해야한다.")
    @Test
    void NULL_결과확인() {
        assertThat(zeroStingConvertor.calculate(null).unmodifiableNumbers())
                .hasSize(1)
                .extracting(number -> number.value())
                .containsExactly(0);
    }

    @DisplayName("공백을 변환한 경우 0을 리턴해야한다.")
    @Test
    void 공백_결과확인() {
        assertThat(zeroStingConvertor.calculate(" ").unmodifiableNumbers())
                .hasSize(1)
                .extracting(number -> number.value())
                .containsExactly(0);
    }

}