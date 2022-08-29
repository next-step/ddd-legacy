package calculator.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddNumbersTest {

    @DisplayName("모든 값이 더해진 결과를 얻을 수 있다.")
    @Test
    void allAddNumbers() {
        // given
        List<String> strings = Arrays.asList("1", "2", "3");

        // when
        AddNumbers numbers = AddNumbers.from(strings);

        // then
        assertThat(numbers.addAllNumbers()).isEqualTo(new AddNumber(6));
    }
}
