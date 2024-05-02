package racingcar;

public class Car {
    private String name;
    private int position;

    public Car(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("자동차 이름은 null 이거나 빈 값일 수 없다.");
        }
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없다.");
        }
        this.name = name;
        this.position = 0;
    }
}
