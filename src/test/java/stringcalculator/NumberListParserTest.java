package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumberListParserTest {

    @DisplayName("컴마나 콜론으로 구분해서 숫자 리스트를 반환한다.")
    @Test
    void parse_comma_colon() {
        NumberList numberList = NumberListParser.parse("1,2:3");
        assertThat(numberList.get(0)).isEqualTo(Number.of(1));
        assertThat(numberList.get(1)).isEqualTo(Number.of(2));
        assertThat(numberList.get(2)).isEqualTo(Number.of(3));
    }


    @DisplayName("//와 \n 문자 사이에 커스텀 구분자를 지정해 숫자 리스트를 반환한다.")
    @Test
    void parse_custom_delimiter() {
        NumberList numberList = NumberListParser.parse("//;\n4;2;9");
        assertThat(numberList.get(0)).isEqualTo(Number.of(4));
        assertThat(numberList.get(1)).isEqualTo(Number.of(2));
        assertThat(numberList.get(2)).isEqualTo(Number.of(9));
    }
}
