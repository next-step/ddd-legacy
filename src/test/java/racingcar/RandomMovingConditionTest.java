package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomMovingConditionTest {

    @Test
    @DisplayName("숫자가 4이상 이면 true를 리턴한다")
    void 숫자가_4이상_이면_true를_리턴한다() {
        assertThat(new RandomMovingCondition().isMovePossible(4)).isTrue();
    }

    @Test
    @DisplayName("숫자가 4미만 이면 false를 리턴한다")
    void 숫자가_4미만_이면_false를_리턴한다() {
        assertThat(new RandomMovingCondition().isMovePossible(3)).isFalse();
    }
}
