package racingcar.test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import racingcar.CarMovingStrategy;
import racingcar.NumberCarMovingStrategy;

class NumberCarMovingStrategyTest {

    @DisplayName("4~9의 값은 이동 가능하다")
    @ParameterizedTest
    @ValueSource(ints= {4,5,6,7,8,9})
    void movableTest(int moveConstant) {
        CarMovingStrategy carMovingStrategy = new NumberCarMovingStrategy(()->moveConstant);
        assertTrue(carMovingStrategy.isMovable());
    }

    @DisplayName("0~3의 값은 이동 불가능하다")
    @ParameterizedTest
    @ValueSource(ints= {0,1,2,3})
    void unmovableTest(int moveConstant) {
        CarMovingStrategy carMovingStrategy = new NumberCarMovingStrategy(()->moveConstant);
        assertFalse(carMovingStrategy.isMovable());
    }
}
