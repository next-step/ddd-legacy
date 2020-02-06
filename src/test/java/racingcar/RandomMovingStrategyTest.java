package racingcar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Random;

import org.junit.jupiter.api.Test;

class RandomMovingStrategyTest {

    @Test
    public void movable_when_random_number_is_3() {
        assertFalse(new RandomMovingStrategy(mockRandomWillReturn(3)).movable());
    }

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