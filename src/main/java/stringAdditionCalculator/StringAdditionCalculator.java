package stringAdditionCalculator;

public class StringAdditionCalculator {
	public int sum(String inputStringNumbers) {
		if (inputStringNumbers == null) {
			return Number.ZERO;
		}

		return getNumbers(inputStringNumbers).getSum();
	}

	private Numbers getNumbers(String inputStringNumbers) {
		return new Numbers(Separator.split(inputStringNumbers));
	}
}
