package racingcar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RandomMovingStrategyTest {

    @DisplayName("무작위 값이 4 보다 작을 경우, 자동차는 움직이지 않는다.")
    @Test
    public void movable_when_random_number_is_3() {
        assertFalse(new RandomMovingStrategy(mockRandomWillReturn(3)).movable());
    }

    @DisplayName("무작위 값이 4 이상일 경우, 자동차는 움직인다.")
    @Test
    public void movable_when_random_number_is_4() {
        assertTrue(new RandomMovingStrategy(mockRandomWillReturn(4)).movable());
    }

    private static Random mockRandomWillReturn(int val) {
        Random mockRandom = mock(Random.class);
        when(mockRandom.nextInt(anyInt())).thenReturn(val);
        return mockRandom;
    }
}