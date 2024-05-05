package stringcalculator;

import java.math.BigInteger;
import java.util.Objects;

public class PositiveInteger {
	public static final PositiveInteger ZERO = new PositiveInteger("0");

	private static final String OPERAND_MUST_NOT_BE_NEGATIVE = "피연산자는 음수가 될 수 없습니다.";

	private static final String INVALID_FORMATTED_OPERAND = "잘못된 형식의 피연산자 입니다.";

	private final BigInteger value;

	public static PositiveInteger valueOf(String value) {
		return new PositiveInteger(value);
	}

	public static PositiveInteger valueOf(BigInteger value) {
		return new PositiveInteger(value);
	}

	private PositiveInteger(String value) {
		this(parseValue(value));
	}

	private PositiveInteger(BigInteger value) {
		if (value.compareTo(BigInteger.ZERO) < 0) {
			throw new RuntimeException(OPERAND_MUST_NOT_BE_NEGATIVE);
		}

		this.value = value;
	}

	private static BigInteger parseValue(String value) {
		try {
			return new BigInteger(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(INVALID_FORMATTED_OPERAND, e);
		}
	}

	public BigInteger value() {
		return value;
	}

	public PositiveInteger add(PositiveInteger other) {
		return new PositiveInteger(value.add(other.value()));
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass())
			return false;
		PositiveInteger that = (PositiveInteger)object;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
