package string_calculator.string_parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import string_calculator.NonNegativeLong;

class SimpleStringParserTest {

    private final SimpleStringParser simpleStringParser = new SimpleStringParser();

    private static LongStream randomNonNegativeLong() {
        return new Random().longs(100)
                .map(Math::abs);
    }

    private static Stream<Character> nonNumericChar() {
        final List<Character> result = new ArrayList<>();
        for (int c = 0x21; c < 0x80; c++) {
            if (isValidCharacter((char) c)) {
                continue;
            }
            result.add((char) c);
        }
        return result.stream();
    }

    private static Stream<Character> invalidDelimiter() {
        final List<Character> result = new ArrayList<>();
        for (int c = 0x01; c < 0x80; c++) {
            if (isValidCharacter((char) c)) {
                continue;
            }
            result.add((char) c);
        }
        return result.stream();
    }

    private static boolean isValidCharacter(final char c) {
        return (c == ',' || c == ':' || ('0' <= c && c <= '9'));
    }

    @DisplayName("string이 null 또는 empty인 경우 빈 배열을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void parse_null_or_empty(final String string) {
        final List<NonNegativeLong> list = this.simpleStringParser.parse(string);
        assertThat(list).isEmpty();
    }

    @DisplayName("string이 blank인 경우 빈 배열을 반환해야 한다.")
    @ValueSource(strings = {"  ", "\t"})
    @ParameterizedTest
    void parse_blank(final String string) {
        final List<NonNegativeLong> list = this.simpleStringParser.parse(string);
        assertThat(list).isEmpty();
    }

    @DisplayName("정수 하나만 있는 문자열을 처리할 수 있어야 한다.")
    @MethodSource("randomNonNegativeLong")
    @ParameterizedTest
    void parse_single_integer(final Long l) {
        final String string = l.toString();

        final List<NonNegativeLong> list = this.simpleStringParser.parse(string);

        assertThat(list).hasSize(1);
        assertThat(list.get(0)).isEqualTo(new NonNegativeLong(l));
    }

    @DisplayName("숫자가 아닌 문자로 구성된 문자열인 경우 runtime exception이 발생해야 한다.")
    @MethodSource("nonNumericChar")
    @ParameterizedTest
    void parse_non_numeric_char(final Character c) {
        final String string = c.toString();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> this.simpleStringParser.parse(string)
        );
    }

    @DisplayName("문자열이 ,와 :로 분리되어야 한다.")
    @ValueSource(strings = {
            "1,2,3,4,5",
            "1:2:3:4:5",
            "1,2:3,4:5",
    })
    @ParameterizedTest
    void parse(final String string) {
        final List<NonNegativeLong> expectedList = new ArrayList<>();
        expectedList.add(new NonNegativeLong(1));
        expectedList.add(new NonNegativeLong(2));
        expectedList.add(new NonNegativeLong(3));
        expectedList.add(new NonNegativeLong(4));
        expectedList.add(new NonNegativeLong(5));

        final List<NonNegativeLong> list = this.simpleStringParser.parse(string);

        assertThat(list).isEqualTo(expectedList);
    }

    @DisplayName("delimiter가 올바르지 않은 경우 runtime exception이 발생해야 한다.")
    @MethodSource("invalidDelimiter")
    @ParameterizedTest
    void parse_invalid_delimiter(final Character delimiter) {
        final String string = "1" + delimiter + "2";

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> this.simpleStringParser.parse(string)
        );
    }
}
