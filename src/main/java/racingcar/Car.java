package racingcar;

class Car {
    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void move(MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
