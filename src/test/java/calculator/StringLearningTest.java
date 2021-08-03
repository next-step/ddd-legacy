package calculator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StringLearningTest {
    @Test
    void temp() {
        String[] split = "1".split(",");
        assertThat(split).isEqualTo(new String[]{"1"});
    }
}
