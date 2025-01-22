package racingcar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomMoveStrategyTest {

    @Test
    @DisplayName("자동차는 숫자 조건이 4 이상이면 움직인다.")
    void movable_success() {
        // given
        int givenNumber = 4;

        // when
        MoveStrategy moveStrategy = new RandomMoveStrategy();
        boolean result = moveStrategy.movable(givenNumber);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("자동차는 숫자 조건이 4 미만이면 움직이지 않는다.")
    void movable_false() {
        // given
        int givenNumber = 3;

        // when
        MoveStrategy moveStrategy = new RandomMoveStrategy();
        boolean result = moveStrategy.movable(givenNumber);

        // then
        assertThat(result).isFalse();
    }
}
