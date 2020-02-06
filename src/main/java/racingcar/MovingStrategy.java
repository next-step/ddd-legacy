package racingcar;

@FunctionalInterface
public interface MovingStrategy {
    boolean movable();
}

// 메서드를 하나만 가진, functional interface -> 람다식 구현 편리
// 근데 이 인터페이스에 함수가 추가된다? 그걸 컴파일 단계 전에 감지하기 위해서
// @FunctionalInterface 붙이면 된다~~

