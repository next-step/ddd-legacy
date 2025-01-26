package racingcar;

/**
 * 현재 위치를 기반으로 차의 이동 값이 결정되는 요구사항을 대비하여 현재 위치도 입력으로 받음
 */
@FunctionalInterface
public interface MoveStrategy {
    CarPosition getNextPosition(CarPosition position, int input);
}
