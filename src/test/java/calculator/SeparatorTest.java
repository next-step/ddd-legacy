package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class SeparatorTest {
    @Test
    @DisplayName("Separator 는 정해진 문자열이 오면 스트링 숫자 리스트로 반환한다.")
    void separateTest() {
        NumberStrings result = Separator.separate(new TargetString("13,2"));

        assertThat(result.getNumbers()).containsExactlyInAnyOrder("13", "2");
    }
}