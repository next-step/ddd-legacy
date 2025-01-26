package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThresholdStrategyTest {

    @Test
    @DisplayName("ThresholdStrategy는 입력값이 임계값 이상일 때 한 칸 이동한다")
    void 임계값_이상() {
        final MoveStrategy thresholdStrategy = new ThresholdStrategy(4);
        final CarPosition beforeActionPosition = new CarPosition(0);

        final CarPosition afterActionPosition = thresholdStrategy.getNextPosition(beforeActionPosition, 5);

        assert afterActionPosition.getCurrentPosition() == beforeActionPosition.getCurrentPosition() + 1;
    }

    @Test
    @DisplayName("ThresholdStrategy는 입력값이 임계값 미만일 때 이동하지 않는다")
    void 임계값_미만() {
        final MoveStrategy thresholdStrategy = new ThresholdStrategy(4);
        final CarPosition beforeActionPosition = new CarPosition(0);

        final CarPosition afterActionPosition = thresholdStrategy.getNextPosition(beforeActionPosition, 3);

        assert afterActionPosition.getCurrentPosition() == beforeActionPosition.getCurrentPosition();
    }
}