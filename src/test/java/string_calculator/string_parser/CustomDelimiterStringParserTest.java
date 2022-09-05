package string_calculator.string_parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import string_calculator.NonNegativeLong;

class CustomDelimiterStringParserTest {

    final CustomDelimiterStringParser customDelimiterStringParser =
            new CustomDelimiterStringParser(":");

    @DisplayName("string이 null 또는 empty인 경우 빈 배열을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void parse_null_or_empty(final String string) {
        final List<NonNegativeLong> list = this.customDelimiterStringParser.parse(string);
        assertThat(list).isEmpty();
    }

    @DisplayName("string이 blank인 경우 빈 배열을 반환해야 한다.")
    @ValueSource(strings = {"  ", "\t"})
    @ParameterizedTest
    void parse_blank(final String string) {
        final List<NonNegativeLong> list = this.customDelimiterStringParser.parse(string);
        assertThat(list).isEmpty();
    }

    @DisplayName("문자열이 ,와 :로 분리되어야 한다.")
    @ValueSource(strings = {"!", "@", "#", "%", "&"})
    @ParameterizedTest
    void parse(final String delimiter) {
        final List<NonNegativeLong> expectedList = new ArrayList<>();
        expectedList.add(new NonNegativeLong(1));
        expectedList.add(new NonNegativeLong(2));
        expectedList.add(new NonNegativeLong(3));

        final List<String> stringList = expectedList.stream()
                .map(NonNegativeLong::value)
                .map(Object::toString)
                .collect(Collectors.toList());
        final String string = "//" + delimiter + "\n" + String.join(delimiter, stringList);

        final CustomDelimiterStringParser customDelimiterStringParser =
                new CustomDelimiterStringParser(delimiter);
        final List<NonNegativeLong> list = customDelimiterStringParser.parse(string);

        assertThat(list).isEqualTo(expectedList);
    }
}
