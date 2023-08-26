package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumberListTest {

    @DisplayName("하나의 숫자 문자열을 입력시 입력한 숫자 문자열을 반환한다.")
    void one_string_sum() {
        NumberList numberList = NumberList.of(List.of(Number.of(10)));
        assertThat(numberList.sum()).isEqualTo(10);
    }

    @DisplayName("두개 이상의 숫자 문자열들의 합을 반환한다.")
    @Test
    void more_two_string_sum() {
        NumberList numberList = NumberList.of(List.of(Number.of(1),Number.of(2),Number.of(3)));
        assertThat(numberList.sum()).isEqualTo(6);
    }

}
