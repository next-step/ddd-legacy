package racingcar;

@FunctionalInterface // 람다를 이용할 수 있음
public interface MoveCondition {
    boolean movable();
}

