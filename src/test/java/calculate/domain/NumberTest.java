package calculate.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberTest {

    @Test
    void 양수로_number_객체_생성_테스트() {
        Number number = new Number(1);

        assertThat(number).isEqualTo(new Number(1));
    }

    @Test
    void 음수로_number_객체_실패_테스트() {
        assertThatThrownBy(() -> new Number(-1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("문자열 계산기에는 음수가 입력될 수 없습니다.");
    }

}
