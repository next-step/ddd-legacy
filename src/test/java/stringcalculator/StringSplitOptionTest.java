package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static stringcalculator.StringSplitOption.CUSTOM_DELIMITER;
import static stringcalculator.StringSplitOption.DEFAULT_DELIMITER;

class StringSplitOptionTest {

    @DisplayName(value = "디폴드 구분자를 가진 문자열로 ENUM 검색")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1,2:3", "1:2:3"})
    void 성공_디폴트_구분자_ENUM_찾기(String numbers) {
        assertThat(StringSplitOption.find(numbers)).isEqualTo(DEFAULT_DELIMITER);
    }

    @DisplayName(value = "커스텀 구분자를 가진 문자열로 ENUM 검색")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//@\n1@2@3", "//&\n1&2&3"})
    void 성공_커스텀_구분자_ENUM_찾기(String numbers) {
        assertThat(StringSplitOption.find(numbers)).isEqualTo(CUSTOM_DELIMITER);
    }

    @DisplayName(value = "디폴트 구분자로 숫자배열 생성")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1,2:3", "1:2:3"})
    void 성공_디폴트_구분자로_문자열_나누기(String numbers) {
        assertThat(DEFAULT_DELIMITER.split(numbers)).containsExactly("1", "2", "3");
    }

    @DisplayName(value = "커스텀 구분자로 숫자배열 생성")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//@\n1@2@3", "//&\n1&2&3"})
    void 성공_커스텀_구분자로_문자열_나누기(String numbers) {
        assertThat(CUSTOM_DELIMITER.split(numbers)).containsExactly("1", "2", "3");
    }
}
