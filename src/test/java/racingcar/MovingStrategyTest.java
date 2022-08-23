package racingcar;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
class MovingStrategyTest {

    @DisplayName("random이 4보다 크거나 같다면 움직일 수 있다.")
    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    void test(int randomNumber) {
        final MovingStrategy movingStrategy = new RandomMovingStrategy(new FakeRandomNumber(randomNumber));
        Assertions.assertTrue(movingStrategy.canMove());
    }

    @DisplayName("random이 4보다 작다면 움직일 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void f(int randomNumber) {
        final MovingStrategy movingStrategy = new RandomMovingStrategy(new FakeRandomNumber(randomNumber));
        Assertions.assertFalse(movingStrategy.canMove());
    }
}
