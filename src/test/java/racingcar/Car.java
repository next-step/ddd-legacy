package racingcar;

public class Car {
    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("이름은 5자를 넘을 수 없습니다.");
        }
    }
}
