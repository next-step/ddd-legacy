package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class StringCalculatorDelimitersTest {
    private StringCalculatorDelimiters sut;
    private int initSize;

    @BeforeEach
    void setUp() {
        sut = StringCalculatorDelimiters.create();
        initSize = sut.getDelimiters().size();
    }

    @DisplayName("최초 생성시 기본 구분자(, :)가 포함되어 있어야 한다.")
    @Test
    void shouldIncludeDefaultDelimiters() {
        assertThat(sut.getRegex()).contains(",", ":");
    }

    @DisplayName("구분자를 추가할 수 있어야 한다.")
    @Test
    void shouldAddCustomDelimiter() {
        sut.addDelimiter(';');

        assertThat(sut.getDelimiters()).contains(';');
    }

    @DisplayName("중복된 구분자를 추가하면 무시되어야 한다.")
    @Test
    void shouldNotAddDuplicateDelimiter() {
        sut.addDelimiter(';');
        sut.addDelimiter(';'); // 중복 추가

        assertThat(sut.getDelimiters()).hasSize(initSize + 1);
    }

    @DisplayName("커스텀 구분자를 입력에서 추출하고 추가할 수 있어야 한다.")
    @ParameterizedTest
    @MethodSource("provideCustomDelimiterInputs")
    void shouldExtractAndAddCustomDelimiter(final String input, final char customDelimiter, final String expectedOutput) {
        int initSize = sut.getDelimiters().size();

        String result = sut.extractAndAddCustomDelimiter(input);

        assertThat(sut.getDelimiters()).hasSize(initSize + 1);
        assertThat(sut.getDelimiters()).contains(customDelimiter);
        assertThat(result).isEqualTo(expectedOutput);
    }

    static Stream<Arguments> provideCustomDelimiterInputs() {
        return Stream.of(
                Arguments.of("//;\n1;2;3", ';', "1;2;3"),
                Arguments.of("//|\n4|5|6", '|', "4|5|6"),
                Arguments.of("//#\n7#8#9", '#', "7#8#9")
        );
    }

    @DisplayName("커스텀 구분자 없이 입력이 그대로 유지되어야 한다.")
    @ParameterizedTest
    @MethodSource("provideNonCustomDelimiterInputs")
    void shouldReturnInputWhenNoCustomDelimiter(final String input) {
        String result = sut.extractAndAddCustomDelimiter(input);

        assertThat(result).isEqualTo(input);
        assertThat(sut.getRegex()).contains(",", ":");
    }

    static Stream<String> provideNonCustomDelimiterInputs() {
        return Stream.of("1,2:3", "7,8:9", "10:11,12");
    }

    @DisplayName("생성된 정규식을 이용해 문자열을 올바르게 분리할 수 있어야 한다.")
    @ParameterizedTest
    @MethodSource("provideInputForRegexSplitting")
    void getRegex_ShouldCorrectlySplitInput(String input, Set<Character> customDelimiters, String[] expectedTokens) {
        customDelimiters.forEach(sut::addDelimiter); // 구분자 추가

        String regex = sut.getRegex();
        String[] actualTokens = input.split(regex);

        assertThat(actualTokens).containsExactly(expectedTokens);
    }

    static Stream<Arguments> provideInputForRegexSplitting() {
        return Stream.of(
                Arguments.of("1,2:3", Set.of(',', ':'), new String[]{"1", "2", "3"}),
                Arguments.of("4;5,6", Set.of(';',','), new String[]{"4", "5", "6"}),
                Arguments.of("7,8:9;10;11", Set.of(',', ':', ';'), new String[]{"7", "8", "9", "10", "11"})
        );
    }

}