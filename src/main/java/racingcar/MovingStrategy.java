package racingcar;

@FunctionalInterface // 컴파일 단계에서 단일 메소드 인터페이스인지 체크해주는 annotation
public interface MovingStrategy {
    boolean movable();
}
