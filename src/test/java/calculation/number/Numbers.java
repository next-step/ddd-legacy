package calculation.number;

import java.util.ArrayList;
import java.util.List;

public final class Numbers {
	public static final Numbers EMPTY = new Numbers(new ArrayList<>());
	private final List<Number> numbers;

	public Numbers(List<Number> numbers) {
		this.numbers = numbers;
	}

	public Numbers() {
		numbers = new ArrayList<>();
		this.numbers.add(Number.EMPTY);
	}

	public int sum() {
		return numbers.stream().map(Number::getValue).reduce(Number.EMPTY.getValue(), Number.EMPTY::sum);
	}
}
