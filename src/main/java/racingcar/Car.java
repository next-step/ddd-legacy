package racingcar;

class Car {

    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
    }

    public void move(final MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public void move(int condition) {
//        final  int condition = new Random().nextInt(10);
        if (condition >= 4) {
            position++;
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}