package stringaddcalculator;

public class NegativeOperandException extends RuntimeException {
    private static final String MESSAGE = "Operand는 음수일 수 없습니다. 현재 값: ";

    public NegativeOperandException(int currentValue) {
        super(MESSAGE + currentValue);
    }
}
