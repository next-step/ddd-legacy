package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {

    @Nested
    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    static class Test1 {
        @ParameterizedTest
        @NullAndEmptySource
        void test1(String input) {


        }
    }


    @Nested
    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다")
    static class Test2 {
        @ParameterizedTest
        @ValueSource(strings = {"0", "1", "10", "100", "1234"})
        void test2(String input) {

        }
    }

    @Nested
    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다. ")
    static class Test3 {
        @ParameterizedTest
        @CsvSource({
                "'0,1'    , 1",
                "'12,42'  , 54",
                "'100,200', 300",
                "'5555,0' , 5555",
                "'123,123', 246"
        })
        void test2(String input, Integer expected) {

        }

    }

    @Nested
    @DisplayName(" //와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    class Test4 {

        @Test
        @DisplayName("")
        void test() {

        }
    }
}
