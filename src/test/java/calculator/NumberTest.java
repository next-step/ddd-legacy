package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberTest {

    @DisplayName("생성된다")
    @Test
    void createTest() {

        Assertions.assertThatCode(() ->  new Number("1"))
                .doesNotThrowAnyException();
    }

    @DisplayName("숫자 이외의 문자열로 생성하면 실패한다")
    @Test
    void createFailedWhenInputNonNumberString() {

        assertThrowsExactly(IllegalArgumentException.class, () -> new Number("a"));
    }

}