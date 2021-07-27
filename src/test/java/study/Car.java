package study;

/*
- 자동차 이름은 5 글자를 넘을 수 없다.
- 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
- 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
public class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
    }

    public void move(final int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
