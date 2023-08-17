package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import calculator.fixture.PositiveStringNumbrersFixture;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveNumberExtractorTest {

    @DisplayName("문자식에서 숫자를 추출한다")
    @ParameterizedTest
    @MethodSource
    void testExtractNumbers(String expression, List<PositiveStringNumber> numbers) {
        // given
        PositiveNumberExtractor sut = new PositiveNumberExtractor();

        // when
        List<PositiveStringNumber> actual = sut.extractNumbers(expression);

        // then
        assertThat(actual).containsExactlyElementsOf(numbers);
    }

    private static Stream<Arguments> testExtractNumbers() {
        return Stream.of(
            Arguments.of("", Collections.emptyList()),
            Arguments.of("1,2,3", PositiveStringNumbrersFixture.create("1", "2", "3")),
            Arguments.of("1,2,3", PositiveStringNumbrersFixture.create("1", "2", "3")),
            Arguments.of("1:2:3", PositiveStringNumbrersFixture.create("1", "2", "3")),
            Arguments.of("1:2:3", PositiveStringNumbrersFixture.create("1", "2", "3")),
            Arguments.of("//;\n1;2;3", PositiveStringNumbrersFixture.create("1", "2", "3"))
        );
    }
}
