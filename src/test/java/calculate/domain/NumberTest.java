package calculate.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberTest {

    @Test
    void 문자열_number_객체_생성_테스트() {
        Number number = new Number("1");

        assertThat(number).isEqualTo(new Number(1));
    }

}
