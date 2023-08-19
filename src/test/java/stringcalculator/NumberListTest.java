package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumberListTest {

    @DisplayName("하나의 숫자 문자열 리스트를 생성한다.")
    @Test
    void one_string() {
        NumberList numberList = NumberList.of(new String[]{"1"});
        assertThat(numberList.get(0)).isEqualTo(Number.of(1));
    }

    @DisplayName("두개 이상의 숫자 문자열들의 리스트를 생성한다.")
    @Test
    void more_two_string() {
        NumberList numberList = NumberList.of(new String[]{"1", "2", "3"});
        assertThat(numberList.get(0)).isEqualTo(Number.of(1));
        assertThat(numberList.get(1)).isEqualTo(Number.of(2));
        assertThat(numberList.get(2)).isEqualTo(Number.of(3));
    }

    @DisplayName("두개 이상의 숫자 문자열들의 합을 반환한다.")
    @Test
    void sum() {
        NumberList numberList = NumberList.of(new String[]{"1", "2", "3"});
        assertThat(numberList.sum()).isEqualTo(6);
    }

}
