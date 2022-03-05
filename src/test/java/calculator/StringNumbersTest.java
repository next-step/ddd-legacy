package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class StringNumbersTest {

    @DisplayName("문자열 리스트를 전달할 경우 숫자 리스트로 변환해 반환한다.")
    @Test
    void parseInt() {
        // given
        Integer[] intArray = IntStream.rangeClosed(1, 5).boxed().toArray(Integer[]::new);
        List<String> stringList = Arrays.stream(intArray)
                .map(n -> Integer.toString(n))
                .collect(Collectors.toList());
        StringNumbers stringNumbers = new StringNumbers(stringList);

        // when
        List<Integer> intNumbers = stringNumbers.parseInt();

        // then
        assertThat(intNumbers).contains(intArray);
    }
}