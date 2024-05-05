package calculator;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

@DisplayName("NumberMapper 클래스")
public class NumberMapperTest {

    private NumberMapper numberMapper;

    @BeforeEach
    void setUp() {
        numberMapper = new NumberMapper();
    }

    @Test
    @DisplayName("정수 문자열 배열을 Number 객체 스트림으로 변환")
    void toNumbers_PositiveCases() {
        String[] tokens = new String[]{"0", "1", "2"};
        Stream<Number> numbers = numberMapper.toNumbers(tokens);

        Assertions.assertThat(numbers)
                .hasSize(3)
                .contains(Number.of("0"), Number.of("1"), Number.of("2"));
    }

    @Test
    @DisplayName("부정확한 숫자 문자열을 변환하려고 할 때 NumberFormatException")
    void toNumbers_InvalidNumbers_ThrowsNumberFormatException() {
        String[] tokens = new String[]{"abc", "xyz"};
        Assertions.assertThatThrownBy(() -> numberMapper.toNumbers(tokens).toArray())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid number");
    }

    @Test
    @DisplayName("음수 문자열을 변환하려고 할 때 RuntimeException")
    void toNumbers_NegativeNumbers_ThrowsRuntimeException() {
        String[] tokens = new String[]{"-1","-2"};
        Assertions.assertThatThrownBy(() -> numberMapper.toNumbers(tokens).toArray())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Number must be positive.");
    }

    @Test
    @DisplayName("빈 배열 입력을 처리")
    void toNumbers_EmptyArray_EmptyStream() {
        String[] tokens = new String[]{};
        Assertions.assertThat(numberMapper.toNumbers(tokens)).isEmpty();
    }

}