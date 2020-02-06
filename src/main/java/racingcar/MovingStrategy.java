package racingcar;

@FunctionalInterface // IDE 레벨에서도 검사가능해욥!
public interface MovingStrategy {
    boolean movable();
}