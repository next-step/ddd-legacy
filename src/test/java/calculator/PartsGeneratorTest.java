package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class PartsGeneratorTest {

    @DisplayName("구분자를 갖지않는 문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"1", "12", "123"})
    void no_delimiter_generate(String input) {
        PartsGenerator generator = getGenerator();
        Parts parts = generator.generate(input);
        assertThat(parts.parts()).containsExactly(input);
    }

    @DisplayName("쉼표 또는 콜론을 구분자로 가지는 문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1:2:3"})
    void delimiter_generate(String input) {
        PartsGenerator generator = getGenerator();
        Parts parts = generator.generate(input);
        assertThat(parts.parts()).containsExactly("1", "2", "3");
    }

    @DisplayName("커스텀 구분자(//{delimiter}\n)를 가지는 문자열 부품을 생성한다")
    @ParameterizedTest
    @ValueSource(strings = {"//-\n1-2-3", "//a\n1a2a3"})
    void custom_delimiter_generate(String input) {
        PartsGenerator generator = getGenerator();
        Parts parts = generator.generate(input);
        assertThat(parts.parts()).containsExactly("1", "2", "3");
    }

    private static PartsGenerator getGenerator() {
        return new PartsGenerator();
    }
}
