package kitchenpos.calculator;

@FunctionalInterface
public interface ValidationStrategy {
    boolean isValid(int num);
}
