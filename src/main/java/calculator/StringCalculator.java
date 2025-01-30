package calculator;

public class StringCalculator {

	public int calculateSum(Input input) {
		if (input == null) {
			return 0;
		}
		return input.getSum();
	}
}
