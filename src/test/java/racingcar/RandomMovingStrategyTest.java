package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static racingcar.RandomMovingStrategy.MOVABLE_THRESHOLD;
import static racingcar.RandomMovingStrategy.RANDOM_VALUE_BOUND;

import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RandomMovingStrategyTest {

    @MethodSource("movable_cases")
    @ParameterizedTest
    @DisplayName("0~"+ RANDOM_VALUE_BOUND + "까지의 무작위 값이 " + MOVABLE_THRESHOLD + "이상일 경우, 자동차는 움직인다.")
    public void movable(int randomValue,
                        boolean expected) {
        assertThat(new RandomMovingStrategy(mockRandomWillReturn(randomValue)).movable())
            .describedAs("무작위 값이 %s일 경우", randomValue)
            .isEqualTo(expected);
    }

    private static Stream<Arguments> movable_cases() {
        return Stream.of(Arguments.of(MOVABLE_THRESHOLD - 1, false),
                         Arguments.of(MOVABLE_THRESHOLD, true),
                         Arguments.of(MOVABLE_THRESHOLD + 1, true));
    }

    private static Random mockRandomWillReturn(int val) {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(anyInt())).thenReturn(val);
        return mockRandom;
    }
}