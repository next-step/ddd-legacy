package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringSplitterTest {
    private StringSplitter sut;

    @BeforeEach
    void setUp() {
        sut = new StringSplitter();
    }

    @DisplayName("입력값을 구분자 단위로 분리한다.")
    @Test
    public void split() {
        Assertions.assertThat(sut.split("1,2,3")).isEqualTo(new String[]{"1", "2", "3"});
    }
}