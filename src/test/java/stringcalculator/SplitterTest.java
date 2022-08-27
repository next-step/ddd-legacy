package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SplitterTest {

    private Splitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new Splitter();
    }

    @DisplayName("//와 \\n 문자 사이에 있는 값을 구분자로 사용하여 분리된 값을 반환한다.")
    @Test
    void custom_split() {
        assertThat(splitter.split("//]\n1]2]3")).containsExactly("1", "2", "3");
    }

    @DisplayName("쉼표(,) 혹은 콜론(:)을 구분자로 사용하여 분리된 값을 반환한다.")
    @Test
    void default_split() {
        assertAll(() -> {
            assertThat(splitter.split("1,2,3")).containsExactly("1", "2", "3");
            assertThat(splitter.split("1:2:3")).containsExactly("1", "2", "3");
            assertThat(splitter.split("1,2:3")).containsExactly("1", "2", "3");
        });
    }
}
