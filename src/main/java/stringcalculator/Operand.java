package stringcalculator;

import java.math.BigInteger;

public class Operand {
	private static final String OPERAND_MUST_NOT_BE_NEGATIVE = "피연산자는 음수가 될 수 없습니다.";

	private static final String INVALID_FORMATTED_OPERAND = "잘못된 형식의 피연산자 입니다.";

	private final BigInteger value;

	public Operand(String value) {
		try {
			this.value = new BigInteger(value);

			if (this.value.compareTo(BigInteger.ZERO) < 0) {
				throw new RuntimeException(OPERAND_MUST_NOT_BE_NEGATIVE);
			}
		} catch (NumberFormatException e) {
			throw new RuntimeException(INVALID_FORMATTED_OPERAND, e);
		}
	}

	public BigInteger value() {
		return value;
	}
}
