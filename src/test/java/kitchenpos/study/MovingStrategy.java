package kitchenpos.study;

@FunctionalInterface // 추상메서드를 하나만 가질 수 있도록 컴파일 level에서 잡아주는 역할
public interface MovingStrategy {
    boolean movable();
}
