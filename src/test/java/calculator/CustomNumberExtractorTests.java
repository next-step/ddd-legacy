package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomNumberExtractorTests {

    CustomNumberExtractor customNumberExtractor;

    @BeforeEach
    public void setup() {
        customNumberExtractor = new CustomNumberExtractor();
    }

    @DisplayName("커스텀 구분자가 있는 경우 숫자 콜렉션 추출")
    @ParameterizedTest
    @ValueSource(strings = {"//!\n112!3:5,523:123!1"})
    void getNumbersWhenHasCustomDivider(String input) {
        List<Integer> numbers = customNumberExtractor.extractNumbers(input);
        assertThat(numbers.get(0)).isEqualTo(112);
        assertThat(numbers.get(3)).isEqualTo(523);
    }

    @DisplayName("커스텀 구분자가 있는 경우 커스텀 구분자 반환")
    @ParameterizedTest
    @ValueSource(strings = {"//!\n112!3:5,523:123!1"})
    void getCustomDivider(String input) {
        String customDivider = customNumberExtractor.getCustomDivider(input);
        assertThat(customDivider).isEqualTo("!");
    }

    @DisplayName("음수가 섞인 경우 RuntimeException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"-112:3:5,523:123,1"})
    void errorOccurWhenContainsMinus(String input) {
        assertThatThrownBy(() -> customNumberExtractor.extractNumbers(input)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("숫자가 아닌 값이 섞인 경우 RuntimeException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"112:hello:5,523:123,1"})
    void errorOccurWhenContainsNotNumber(String input) {
        assertThatThrownBy(() -> customNumberExtractor.extractNumbers(input)).isInstanceOf(RuntimeException.class);
    }
}