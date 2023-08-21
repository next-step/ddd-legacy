package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberStingConvertorTest {

    private NumberStingConvertor numberStingConvertor;

    @BeforeEach
    void setUp() {
        numberStingConvertor = new NumberStingConvertor();
    }

    @DisplayName("숫자 하나를 지원해야한다.")
    @Test
    void 지원정책확인() {
        assertThat(numberStingConvertor.isSupport("1")).isTrue();
        assertThat(numberStingConvertor.isSupport("-1")).isTrue();
    }

    @DisplayName("양수가 아닐경우 에러를 리턴한다.")
    @Test
    void 양수가아닌값에러() {
        assertThatThrownBy(() -> numberStingConvertor.calculate("-1"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("'1'을 변환한 경우 1을 리턴해야한다.")
    @Test
    void 결과확인() {
        assertThat(numberStingConvertor.calculate("1").unmodifiableNumbers())
                .hasSize(1)
                .extracting(number -> number.value())
                .containsExactly(1);
    }

}