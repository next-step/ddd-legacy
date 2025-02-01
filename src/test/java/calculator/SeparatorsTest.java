package calculator;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SeparatorsTest {

    @Test
    @DisplayName("구분자를 (:,)로 지정하고 구분")
    void separate() {
        final var separators = Separators.of(Set.of(":", ","));

        assertThat(separators.separate("1:2,3"))
            .contains(1, 2, 3);
    }

    @Test
    @DisplayName("구분자 (;)를 추가하고 구분")
    void addSeparator() {
        final var separators = Separators.of(Set.of(":", ","));

        separators.addSeparator(";");
        assertThat(separators.separate("1:2,3;4"))
            .contains(1, 2, 3, 4);
    }

    @Test
    @DisplayName("구분자없이 생성하면 에러")
    void emptySeparator() {
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> Separators.of(Set.of()));
    }
}
