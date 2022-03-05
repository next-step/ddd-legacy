package calculation.number;


import java.util.Objects;

public final class Number {
	private static final int NUMBER_ZERO = 0;
	public static final Number EMPTY = new Number(NUMBER_ZERO);
	private static final String NEGATIVE_NUMBER_ARE_NOT_ALLOWED = "음수는 허용되지 않습니다.";
	private static final String THIS_DELIMITER_ARE_NOT_ALLOWED = "지정된 특수문자 외에 들어갈 수 없습니다.";
	private final int value;

	private Number(int value) {
		if (value < NUMBER_ZERO) {
			throw new RuntimeException(NEGATIVE_NUMBER_ARE_NOT_ALLOWED + " : " + value);
		}
		this.value = value;
	}

	public static Number convertStringToNumber(String string) {
		if (Objects.isNull(string) || string.trim().isEmpty()) {
			return Number.EMPTY;
		}
		try {
			int parseInt = Integer.parseInt(string);
			return new Number(parseInt);
		} catch (NumberFormatException e) {
			throw new RuntimeException(THIS_DELIMITER_ARE_NOT_ALLOWED + " : " + string);
		}
	}

	public int getValue() {
		return value;
	}

	public int sum(int a, int b) {
		return a + b;
	}
}
