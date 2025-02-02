package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class SeparatorParserTest {

    SeparatorParser separatorParser;

    @BeforeEach
    void init() {
        separatorParser = new SeparatorParser(Pattern.compile("//(.+?)\\n(.*)"));
    }

    @DisplayName(value = "//와 \n 문자 사이에 커스텀 구분자를 파싱하여 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customParser(final String input) {
        // when
        CustomSeparator sut = separatorParser.parseSeparator(input);

        // then
        assertThat(sut.getSeparator()).isEqualTo(";");
    }

    @DisplayName(value = "//와 \n 문자 사이에 커스텀 구분자가 없으면 구분자가 null 로 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//\n1;2;3"})
    void customParserEmpty(final String input) {
        // when
        CustomSeparator sut = separatorParser.parseSeparator(input);

        // then
        assertThat(sut.getSeparator()).isNull();
    }


    @DisplayName(value = "약속된 커스텀 구분자가 있으면 인식하고 true 를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void hasCustomParser(final String input) {
        // when
        boolean sut = separatorParser.hasCustomSeparator(input);

        // then
        assertThat(sut).isTrue();
    }

    @DisplayName(value = "약속된 커스텀 구분자가 없으면 false 를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/;\n1;2;3"})
    void hasNotCustomParser(final String input) {
        // when
        boolean sut = separatorParser.hasCustomSeparator(input);

        // then
        assertThat(sut).isFalse();
    }

}