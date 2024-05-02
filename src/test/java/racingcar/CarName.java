package racingcar;


public record CarName(String name) {
    private static final int MAX_CAR_NAME_LENGTH = 5;

    public CarName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null 은 CarName 에 할당될 수 없습니다");
        }
        if (name.length() > MAX_CAR_NAME_LENGTH) {
            throw new IllegalArgumentException("CarName 은 5글자를 넘길 수 없습니다");
        }
        this.name = name;
    }
}
