package calculation.calculator;

public class CalculationResult {
	private final int result;

	public CalculationResult(int result) {
		this.result = result;
	}

	public CalculationResult() {
		this(0);
	}

	public int getResult() {
		return result;
	}
}
