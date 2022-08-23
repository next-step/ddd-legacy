package racingcar;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;

    public Car(final String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 5자를 넘을 수 없습니다.");
        }
    }
}
