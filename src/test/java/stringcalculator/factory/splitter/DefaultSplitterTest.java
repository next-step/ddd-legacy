package stringcalculator.factory.splitter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class DefaultSplitterTest {

    @DisplayName("쉼표(,) 혹은 콜론(:)을 구분자로 사용하여 분리된 값을 반환한다.")
    @Test
    void split() {
        DefaultSplitter defaultSplitter = new DefaultSplitter();

        assertAll(() -> {
            assertThat(defaultSplitter.split("1,2,3")).containsExactly("1", "2", "3");
            assertThat(defaultSplitter.split("1:2:3")).containsExactly("1", "2", "3");
            assertThat(defaultSplitter.split("1,2:3")).containsExactly("1", "2", "3");
        });
    }
}