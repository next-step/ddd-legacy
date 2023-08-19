package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TokenizerTest {

    private TokenizePolicy tokenizePolicyImpl;

    @BeforeEach
    public void init() {
        tokenizePolicyImpl = new TokenizePolicyImpl();
    }

    @DisplayName(",를 통해 분리를 할수 있어야 한다")
    @ParameterizedTest
    @MethodSource("test1MethodSource")
    void test1(String input, String[] expect) {

        //when
        String[] result = tokenizePolicyImpl.parse(input);

        //then
        assertThat(result).containsExactly(expect);
    }

    static Stream<Arguments> test1MethodSource() {
        return Stream.of(
            Arguments.of("1,2,3", new String[]{"1", "2", "3"}),
            Arguments.of("1", new String[]{"1"}),
            Arguments.of("1,2", new String[]{"1", "2"})
        );
    }

    @DisplayName(":를 통해 분리를 할수 있어야 한다")
    @ParameterizedTest
    @MethodSource("test2MethodSource")
    void test2(String input, String[] expect) {

        //when
        String[] result = tokenizePolicyImpl.parse(input);

        //then
        assertThat(result).containsExactly(expect);
    }

    static Stream<Arguments> test2MethodSource() {
        return Stream.of(
            Arguments.of("1:2:3", new String[]{"1", "2", "3"}),
            Arguments.of("1", new String[]{"1"}),
            Arguments.of("1:2", new String[]{"1", "2"})
        );
    }

    @DisplayName(":과 ,를 동시에 사용해 분리를 할수 있어야 한다")
    @ParameterizedTest
    @MethodSource("test3MethodSource")
    void test3(String input, String[] expect) {

        //when
        String[] result = tokenizePolicyImpl.parse(input);

        //then
        assertThat(result).containsExactly(expect);
    }

    static Stream<Arguments> test3MethodSource() {
        return Stream.of(
            Arguments.of("1:2,3", new String[]{"1", "2", "3"}),
            Arguments.of("1", new String[]{"1"}),
            Arguments.of("1,2:3:4", new String[]{"1", "2", "3", "4"})
        );
    }

    @DisplayName("Custom Partitioner를 사용해 분리를 할수 있어야 한다")
    @ParameterizedTest
    @MethodSource("test4MethodSource")
    void test4(String input, String[] expect) {

        //when
        String[] result = tokenizePolicyImpl.parse(input);

        //then
        assertThat(result).containsExactly(expect);
    }

    static Stream<Arguments> test4MethodSource() {
        return Stream.of(
            Arguments.of("//;\n1;2;3", new String[]{"1", "2", "3"}),
            Arguments.of("//!\n1!2!3", new String[]{"1", "2", "3"}),
            Arguments.of("//%\n1%2%3", new String[]{"1", "2", "3"})
        );
    }
}
