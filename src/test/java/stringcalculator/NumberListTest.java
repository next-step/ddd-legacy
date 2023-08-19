package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumberListTest {

    @DisplayName("하나의 숫자 문자열을 반환한다.")
    @Test
    void one_string_sum() {
        NumberList numberList = NumberList.of(new String[]{"1"});
        int result = numberList.sum();
        assertThat(result).isEqualTo(1);
    }

    @DisplayName("두개 이상의 숫자 문자열들의 합을 반환한다.")
    @Test
    void sum() {
        NumberList numberList = NumberList.of(new String[]{"1","2","3"});
        int result = numberList.sum();
        assertThat(result).isEqualTo(6);
    }

}
