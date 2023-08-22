package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NumberParsePolicyTest {

    private NumberParsePolicy numberParsePolicy;

    @BeforeEach
    public void init() {
        numberParsePolicy = new NumberParsePolicyImpl();
    }

    @DisplayName("String 배열에 대해 각각 Int 배열로 변환해주어야 한다.")
    @ParameterizedTest
    @MethodSource("test1MethodSource")
    void test1(String[] input, Integer[] expect) {
        //when
        int[] result = numberParsePolicy.parse(input);

        //then
        assertThat(result).containsExactly(expect);
    }

    static Stream<Arguments> test1MethodSource() {
        return Stream.of(
            Arguments.of(new String[]{"1", "2", "3"}, new Integer[]{1, 2, 3}),
            Arguments.of(new String[]{"1"}, new Integer[]{1}),
            Arguments.of(new String[]{"1", "2"}, new Integer[]{1, 2})
        );
    }

    @DisplayName("숫자로 변환이 불가능한 입력이 있을시 에러를 발생시킨다.")
    @Test
    void test2() {
        //given
        String[] input = {"1l;", "2", "3"};

        //when && then
        assertThatThrownBy(() -> numberParsePolicy.parse(input))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("숫자 변환에 실패 하였습니다.");
    }

    @DisplayName("음수 입력이 있을시 에러를 발생시킨다.")
    @Test
    void test3() {
        //given
        String[] input = {"-1", "2", "3"};

        //when && then
        assertThatThrownBy(() -> numberParsePolicy.parse(input))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("음수는 변환할수 없습니다.");
    }
}