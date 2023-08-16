package racingcar;

public class Car {
    private String name;
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 다섯 글자를 넘읋 수 없습니다.");
        }
        this.name = name;
    }

    public void move(final int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public void move(final MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
