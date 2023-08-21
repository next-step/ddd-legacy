package calculator.vo;


public class Answer {
    public final Integer value;

    private Answer(Integer value) {
        this.value = value;
    }

    public static Answer empty() {
        return new Answer(0);
    }

    public static Answer of(int value) {
        return new Answer(value);
    }
}
