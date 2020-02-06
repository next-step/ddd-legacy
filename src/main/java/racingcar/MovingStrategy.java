package racingcar;

@FunctionalInterface    // 메소드가 하나인 인터페이스를 명시적으로 표기
public interface MovingStrategy {
    boolean movable();
}
