package calculator;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SplitterTest {

    @DisplayName("기본 구분자(,:)와 커스텀 구분자로 문자열이 분리된다.")
    @ParameterizedTest
    @CsvSource(value = {"'1,2:3','123'", "'//;\n7;4;3','743'"})
    void delimiter(String delimiter, String result) {
        String splittedString = new Splitter(delimiter)
            .splittingText()
            .collect(Collectors.joining());
        assertThat(splittedString)
            .isEqualTo(result);
    }
}