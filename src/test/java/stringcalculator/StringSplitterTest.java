package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSplitterTest {
    private StringSplitter splitter;

    @BeforeEach
    void setUp() {
        splitter = new StringSplitter();
    }

    @DisplayName("구분자에 따라 문자를 분리할 수 있다.")
    @Test
    void split() {
        assertThat(splitter.split("1,2:3")).containsAll(Arrays.asList("1", "2","3"));
    }

    @DisplayName("구분자를 추가하여 문자를 분리할 수 있다.")
    @Test
    void addDelimiter_and_split() {
        assertThat(splitter.split("//3\n1?2:34")).containsAll(Arrays.asList("1?2", "4"));
    }

    @DisplayName("특별한 구분자(?, |, ., $)에 대해서도 문자를 구분할 수 있다.")
    @Test
    void addSpecial_and_split() {
        assertThat(splitter.split("//?\n1?2:34")).containsAll(Arrays.asList("1", "2", "34"));
    }
}
