package calculation.number;

import java.util.ArrayList;
import java.util.List;

public final class Numbers {
	public static final Numbers EMPTY = new Numbers(new ArrayList<>());
	private final List<Number> numbers;

	public Numbers(List<Number> numbers) {
		this.numbers = numbers;
	}

	public int sum() {
		return numbers.stream().mapToInt(Number::getValue).sum();
	}
}
