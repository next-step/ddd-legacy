package calculator.calculate;

public class AddCalculateStrategy implements CalculateStrategy {

    @Override
    public int calculate(int first, int second) {
        return first + second;
    }
}
