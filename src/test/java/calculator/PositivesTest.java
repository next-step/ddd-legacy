package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class PositivesTest {
    @DisplayName("숫자 리스트의 합계를 반환한다.")
    @Test
    void sum() {
        // given
        List<Integer> intNumbers = IntStream.rangeClosed(1, 5).boxed().collect(Collectors.toList());

        // when
        Positives positives = new Positives(intNumbers);
        int sum = positives.sum();

        // then
        assertThat(sum).isEqualTo(15);
    }
}