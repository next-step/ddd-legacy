package calculation.number;

import java.util.Collections;
import java.util.List;

public class Numbers {
	private final List<Number> numbers;

	public Numbers(List<Number> numbers) {
		this.numbers = numbers;
	}

	public int sum() {
		return numbers.stream().map(Number::getValue).reduce(new Number().getValue(), Number::sum);
	}
}
