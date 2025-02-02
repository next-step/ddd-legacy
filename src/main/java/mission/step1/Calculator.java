package mission.step1;

public class Calculator implements Calculable {

    private PositiveNumber accumulated;

    public Calculator() {
        this.accumulated = PositiveNumber.from("0");
    }

    @Override
    public PositiveNumber calculate(PositiveNumber number) {
        this.accumulated = PositiveNumber.from(String.valueOf(accumulated.getValue() + number.getValue()));
        return this.accumulated;
    }

    public int getResult() {
        return this.accumulated.getValue();
    }
}
