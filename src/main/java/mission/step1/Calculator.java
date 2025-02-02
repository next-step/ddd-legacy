package mission.step1;

public class Calculator implements Calculable {

    @Override
    public PositiveNumber calculate(PositiveNumber current, PositiveNumber number) {
        return PositiveNumber.from(String.valueOf(current.getValue() + number.getValue()));
    }
}
