package racingcar;

@FunctionalInterface // 2개 이상의 추상메소드 만들지 못하도록 선언!
public interface MovingStrategy {
    boolean movable();
}
