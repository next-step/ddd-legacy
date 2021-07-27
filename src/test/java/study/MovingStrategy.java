package study;

@FunctionalInterface // NOTE: 추상 메소드를 하나만 갖는 경우, 두개 넣으면 컴파일 오류
public interface MovingStrategy {
    boolean movable();
}
