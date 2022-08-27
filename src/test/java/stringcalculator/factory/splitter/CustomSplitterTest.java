package stringcalculator.factory.splitter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomSplitterTest {

    @DisplayName("//와 \\n 문자 사이에 있는 값을 구분자로 사용하여 분리된 값을 반환한다.")
    @Test
    void split() {
        assertThat(new CustomSplitter().split("//]\n1]2]3")).containsExactly("1", "2", "3");
    }
}
