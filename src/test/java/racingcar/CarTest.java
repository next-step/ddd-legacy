package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CarTest {

    @DisplayName("임계값_4의_threshold_전략을_가진_차는_입력값이_4이상일_때_한칸_이동한다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    void threshold_전략을_가진_차(final int input) {
        final int initPosition = 0;
        final int threshold = 4;
        final Car car = new Car("레진코믹스", initPosition, new ThresholdStrategy(threshold));

        car.move(input);

        if (input >= threshold) {
            assertEquals(1, car.getPosition());
        }
        if (input < threshold) {
            assertEquals(0, car.getPosition());
        }
    }
}