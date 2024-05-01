package racingcar;

public class Car {

    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {

        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.position = position; // 인스턴스 생성 시 위치는 0 으로 초기화
    }

    public int getPosition() {
        return position;
    }

    // 확장을 위한 인터페이스 생섬 및 함수 생성
    public void moving(final MovingStrategy condition) {
        if (condition.movable()) {
            position++;
        }
    }

}