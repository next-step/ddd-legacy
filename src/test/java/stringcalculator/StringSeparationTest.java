package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringSeparationTest {

    private StringSeparation separation;

    @BeforeEach
    void setUp() {
        separation = new StringSeparation();
    }

    @ParameterizedTest
    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 PositiveNumber.ZERO 를 반환해야 한다.")
    @NullAndEmptySource
    void nullOrEmpty(String text) {
        assertThat(separation.separate(text)).containsExactly(new PositiveNumber("0"));
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 PositiveNumber 로 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void singleNumber(String text) {
        assertThat(separation.separate(text)).containsExactly(new PositiveNumber(text));
    }

    @DisplayName(value = "쉼표(,)를 구분자로 사용할 수 있다.")
    @ParameterizedTest
    @MethodSource
    void comma(String text, PositiveNumber... expected) {
        assertThat(separation.separate(text)).containsExactly(expected);
    }

    static Stream<Arguments> comma() {
        return Stream.of(
                Arguments.of(
                        "1,2", new PositiveNumber[] {new PositiveNumber("1"), new PositiveNumber("2")}
                ),
                Arguments.of(
                        "3,5", new PositiveNumber[] {new PositiveNumber("3"), new PositiveNumber("5")}
                )
        );
    }

    @DisplayName(value = "콜론(:)을 구분자로 사용할 수 있다.")
    @ParameterizedTest
    @MethodSource
    void colons(String text, PositiveNumber... expected) {
        assertThat(separation.separate(text)).containsExactly(expected);
    }

    static Stream<Arguments> colons() {
        return Stream.of(
                Arguments.of(
                        "1:2", new PositiveNumber[] {new PositiveNumber("1"), new PositiveNumber("2")}
                ),
                Arguments.of(
                        "3:5", new PositiveNumber[] {new PositiveNumber("3"), new PositiveNumber("5")}
                )
        );
    }

    @DisplayName(value = "//와 \n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @MethodSource
    void custom(String text, PositiveNumber... expected) {
        assertThat(separation.separate(text)).containsExactly(expected);
    }

    static Stream<Arguments> custom() {
        return Stream.of(
                Arguments.of(
                        "//;\n1;2", new PositiveNumber[] {new PositiveNumber("1"), new PositiveNumber("2")}
                ),
                Arguments.of(
                        "//&\n3&5", new PositiveNumber[] {new PositiveNumber("3"), new PositiveNumber("5")}
                )
        );
    }
}