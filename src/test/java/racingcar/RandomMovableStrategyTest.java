package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class RandomMovableStrategyTest {


    @DisplayName("주어진 값이 4이상이면 참을 반환한다")
    @Test
    void execute01() {
        RandomMovableStrategy strategy = new RandomMovableStrategy(new Random() {
            @Override
            public int nextInt(int bound) {
                return 4;
            }
        });
        assertThat(strategy.movable()).isTrue();
    }

    @DisplayName("주어진 값이 3이하이면 거짓을 반환한다.")
    @Test
    void execute02() {
        RandomMovableStrategy strategy = new RandomMovableStrategy(new Random() {
            @Override
            public int nextInt(int bound) {
                return 3;
            }
        });
        assertThat(strategy.movable()).isFalse();
    }

}