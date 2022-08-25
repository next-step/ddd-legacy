package racingcar;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;
    private static final String MAX_NAME_LENGTH_MESSAGE = "자동차 이름은 5글자보다 작거나 같아야 합니다.";
    private static final String EMPTY_NAME_MESSAGE = "자동차 이름은 비어있을 수 없습니다.";

    private String name;

    public Car(String name) {
        if (isEmpty(name)) {
            throw new IllegalArgumentException(EMPTY_NAME_MESSAGE);
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(MAX_NAME_LENGTH_MESSAGE);
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    private boolean isEmpty(String name) {
        return name == null || name.isBlank();
    }
}
