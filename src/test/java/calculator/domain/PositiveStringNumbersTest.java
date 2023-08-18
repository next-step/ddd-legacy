package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import calculator.fixture.PositiveStringNumbersFixture;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveStringNumbersTest {

    @DisplayName("PositiveStringNumbers 컬렉션의 합산을 구한다")
    @ParameterizedTest
    @MethodSource
    void testAddAll(String[] values, PositiveStringNumber expected) {
        // given
        PositiveStringNumbers positiveStringNumbers = PositiveStringNumbersFixture.create(values);

        // when
        PositiveStringNumber actual = positiveStringNumbers.addAll();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> testAddAll() {
        return Stream.of(
            Arguments.of(new String[]{}, PositiveStringNumber.ZERO),
            Arguments.of(new String[]{"1"}, PositiveStringNumber.of("1")),
            Arguments.of(new String[]{"1", "2", "3"}, PositiveStringNumber.of("6"))
        );
    }
}
