package addstring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NumbersTest {


    @Test
    @DisplayName("문자열 숫자 배열을 성공적으로 계산한다.")
    void convertStringNumbersToIntSumTest() {
        String[] array = new String[]{"1", "2", "3"};
        List<Number> numberList = Arrays.stream(array)
            .map(Number::new)
            .collect(Collectors.toList());

        Numbers numbers = new Numbers(numberList);
        assertThat(numbers.sum()).isEqualTo(6);
    }


}
