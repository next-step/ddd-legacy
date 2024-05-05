package stringcalculator;

import java.math.BigInteger;
import java.util.Objects;

public class NonNegativeInteger {
	public static final NonNegativeInteger ZERO = new NonNegativeInteger("0");

	private static final String OPERAND_MUST_NOT_BE_NEGATIVE = "피연산자는 음수가 될 수 없습니다.";

	private static final String INVALID_FORMATTED_OPERAND = "잘못된 형식의 피연산자 입니다.";

	private final BigInteger value;

	public static NonNegativeInteger valueOf(String value) {
		return new NonNegativeInteger(value);
	}

	public static NonNegativeInteger valueOf(BigInteger value) {
		return new NonNegativeInteger(value);
	}

	private NonNegativeInteger(String value) {
		this(parseValue(value));
	}

	private NonNegativeInteger(BigInteger value) {
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

	public NonNegativeInteger add(NonNegativeInteger other) {
		return new NonNegativeInteger(value.add(other.value()));
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass())
			return false;
		NonNegativeInteger that = (NonNegativeInteger)object;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
