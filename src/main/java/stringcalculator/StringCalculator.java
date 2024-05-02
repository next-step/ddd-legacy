package stringcalculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
	private final OperandParser parser = new OperandParser();

	private final List<Operand> operands = new ArrayList<>();

	public StringCalculator(final String input) {
		this.operands.addAll(parser.parse(input));
	}

	public BigInteger calculate() {
		return operands.stream()
			.map(Operand::value)
			.reduce(BigInteger.ZERO, BigInteger::add);
	}
}
