package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import calculator.fixture.PositiveStringNumbersFixture;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveNumberExtractorTest {

    @DisplayName("문자식에서 숫자를 추출한다")
    @ParameterizedTest
    @MethodSource
    void testExtractNumbers(String expression, PositiveStringNumbers numbers) {
        // given
        PositiveNumberExtractor sut = new PositiveNumberExtractor();

        // when
        PositiveStringNumbers actual = sut.extractNumbers(expression);

        // then
        assertThat(actual.getValues()).containsExactlyElementsOf(numbers.getValues());
    }

    private static Stream<Arguments> testExtractNumbers() {
        return Stream.of(
            Arguments.of("", PositiveStringNumbers.EMPTY_POSITIVE_STRING_NUMBERS),
            Arguments.of("1,2,3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("1,2,3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("1:2:3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("1:2:3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("//;\n1;2;3", PositiveStringNumbersFixture.create("1", "2", "3"))
        );
    }
}
