package calculator.domain;

import calculator.fixture.PositiveStringNumbersFixture;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveStringNumbersTest {

    @DisplayName("PositiveStringNumbers 컬렉션의 합산을 구한다")
    @ParameterizedTest
    @MethodSource
    void testAddAll(String a, String b, String c) {
        // given
        PositiveStringNumbers positiveStringNumbers = PositiveStringNumbersFixture.create(a, b, c);

        // when
        System.out.println(positiveStringNumbers);

        // then
    }

    private static Stream<Arguments> testAddAll() {
        return Stream.of(
            Arguments.of(List.of(""), PositiveStringNumbers.EMPTY_POSITIVE_STRING_NUMBERS),
            Arguments.of("1,2,3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("1,2,3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("1:2:3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("1:2:3", PositiveStringNumbersFixture.create("1", "2", "3")),
            Arguments.of("//;\n1;2;3", PositiveStringNumbersFixture.create("1", "2", "3"))
        );
    }


}
