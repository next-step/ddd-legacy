package racingcar;

public interface MovingStrategy {
    int _MOVABLE_POSITION_VALUE = 4;        // 이동가능한 POSITION 경계값

    boolean isMovable(int position);
}
