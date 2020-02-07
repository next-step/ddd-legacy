package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultNumberExtractorTests {

    DefaultNumberExtractor defaultNumberExtractor;

    @BeforeEach
    public void setup() {
        defaultNumberExtractor = new DefaultNumberExtractor();
    }

    @DisplayName("입력값 중 기본 구분자(:와 쉼표)를 제외한 숫자만 골라낸 콜렉션 출력")
    @ParameterizedTest
    @ValueSource(strings = {"112:3:5,523:123,1", "112:3:5:523:123:1", "112,3,5,523,123,1"})
    void compositeParser(String input) {
        List<Integer> numbers = defaultNumberExtractor.extractNumbers(input);
        assertThat(numbers.get(0)).isEqualTo(112);
        assertThat(numbers.get(3)).isEqualTo(523);
    }

    @DisplayName("음수가 섞인 경우 RuntimeException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"-112:3:5,523:123,1"})
    void errorOccurWhenContainsMinus(String input) {
        assertThatThrownBy(() -> defaultNumberExtractor.extractNumbers(input)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("숫자가 아닌 값이 섞인 경우 RuntimeException 발생")
    @ParameterizedTest
    @ValueSource(strings = {"112:hello:5,523:123,1"})
    void errorOccurWhenContainsNotNumber(String input) {
        assertThatThrownBy(() -> defaultNumberExtractor.extractNumbers(input)).isInstanceOf(RuntimeException.class);
    }
}