package stringcalculator.operator;

public class OperatorNotFoundMessage {

    private final String input;

    public OperatorNotFoundMessage(String input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return String.format("계산 할 수 없는 형태의 입력값입니다. : %s", input);
    }

}
