package caculator.domain;

public class StringNumberException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "숫자 이외의 값은 계산할 수 없습니다. 입력 값 : %s";
    private static final String NEGATIVE_MESSAGE = "음수는 계산할 수 없습니다. 입력 값 : %s";

    public StringNumberException(String number) {
        super(String.format(DEFAULT_MESSAGE, number));
    }

    public StringNumberException(int number) {
        super(String.format(NEGATIVE_MESSAGE, number));
    }
}
