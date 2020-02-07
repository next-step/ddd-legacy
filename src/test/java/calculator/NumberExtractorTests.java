package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberExtractorTests {

    NumberExtractor numberExtractor;

    @DisplayName("입력값 중 기본 구분자(:와 쉼표)를 제외한 숫자만 골라낸 콜렉션 출력")
    @ParameterizedTest
    @ValueSource(strings = {"112:3:5,523:123,1", "112:3:5:523:123:1", "112,3,5,523,123,1"})
    void compositeParser(String input) {
        numberExtractor = new NumberExtractor(input);
        List<Integer> numbers = numberExtractor.extractNumbers();
        assertThat(numbers.get(0)).isEqualTo(112);
        assertThat(numbers.get(3)).isEqualTo(523);
    }

    @DisplayName("커스텀 구분자가 있는 경우 숫자 콜렉션 추출")
    @ParameterizedTest
    @ValueSource(strings = {"//!\n112!3:5,523:123!1"})
    void getNumbersWhenHasCustomDivider(String input) {
        numberExtractor = new NumberExtractor(input);
        List<Integer> numbers = numberExtractor.extractNumbers();
        assertThat(numbers.get(0)).isEqualTo(112);
        assertThat(numbers.get(3)).isEqualTo(523);
    }

    @DisplayName("음수가 섞인 경우 RuntimeException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"-112:3:5,523:123,1"})
    void errorOccurWhenContainsMinus(String input) {
        numberExtractor = new NumberExtractor(input);
        assertThatThrownBy(() -> numberExtractor.extractNumbers()).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("숫자가 아닌 값이 섞인 경우 RuntimeException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"112:hello:5,523:123,1"})
    void errorOccurWhenContainsNotNumber(String input) {
        numberExtractor = new NumberExtractor(input);
        assertThatThrownBy(() -> numberExtractor.extractNumbers()).isInstanceOf(RuntimeException.class);
    }
}