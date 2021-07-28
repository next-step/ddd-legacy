package calculator.test;

import calculator.TextSeparator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TextSeparatorTest {
    @DisplayName("커스텀 구분자와 숫자 분리")
    @Test
    void separate() {
        String text = "//;\n6";
        TextSeparator separator = new TextSeparator(text);

        assertThat(separator.getSeparator()).isEqualTo(";");
        assertThat(separator.getNumbers().equals(Collections.singletonList("6"))).isTrue();
    }

    @DisplayName("커스텀 구분자 없으면 , or : 를 이용해 분리")
    @Test
    void separate_no_custom_separator() {
        String text = "1,2:3";
        TextSeparator separator = new TextSeparator(text);

        assertThat(separator.getSeparator()).isEqualTo(",|:");
        assertThat(separator.getNumbers().equals(List.of("1", "2", "3"))).isTrue();
    }
}
