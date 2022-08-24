package racingcar;

public class Car{

    private String name;

    private Car(String name) {
        this.name = name;
    }

    public static Car of(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘길 수 없습니다.");
        }
        return new Car(name);
    }

}
