package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarPositionTest {

    @ParameterizedTest
    @DisplayName("차 위치 생성 시, 입력값이 초기 위치다")
    @ValueSource(ints = {0, 1, -1})
    void 차_위치_초기화(final int initPosition) {
        final CarPosition carPosition = new CarPosition(initPosition);

        assert carPosition.getCurrentPosition() == initPosition;
    }

    @ParameterizedTest
    @DisplayName("차 위치 이동 시, 기존 위치에서 입력 값 만큼 이동한다")
    @ValueSource(ints = {0, 1, -1})
    void 차_위치_이동(final int movingDistance) {
        final int initPosition = 10;
        final CarPosition beforeMovePosition = new CarPosition(initPosition);
        final CarPosition afterMovePosition = beforeMovePosition.move(movingDistance);

        assert afterMovePosition.getCurrentPosition() == initPosition + movingDistance;
    }
}