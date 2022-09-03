package string_calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ListCalculatorTest {

    @DisplayName("임의의 정수로 이루어진 임의의 길이를 가진 list로 초기화할 수 있다.")
    @Test
    void construct_with_any_list() {
        final List<Long> list = new ArrayList<>();
        assertThatNoException().isThrownBy(() -> new ListCalculator(list));
    }

    @DisplayName("빈 list로 초기화할 수 있다.")
    @Test
    void construct_with_empty_list() {
        final List<Long> list = new ArrayList<>();
        assertThatNoException().isThrownBy(() -> new ListCalculator(list));
    }

    @DisplayName("null인 list로 초기화할 수 없다.")
    @Test
    void construct_with_null_list() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ListCalculator(null));
    }

    @DisplayName("리스트 원소의 합을 올바르게 반환해야 한다.")
    @Test
    void sum_correct_value() {
        final List<Long> list = new ArrayList<>();
        for (long i = 1; i < 11; i++) {
            list.add(i);
        }

        final ListCalculator listCalculator = new ListCalculator(list);
        assertThat(listCalculator.sum()).isEqualTo(55);
    }
}
