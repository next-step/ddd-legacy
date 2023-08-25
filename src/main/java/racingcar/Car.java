package racingcar;

public class Car {

  private final String name;
  private int position;

  public Car(String name) {
    if (name.length() > 5) {
      throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다");
    }
    this.name = name;
    this.position = 0;
  }

  public void move(final int condition) {
    if (condition >= 4) {
      position++;
    }
  }

  public void move(final MoveCondition condition) {
    if (condition.movable()) {
      position++;
      return;
    }

    position = 0;
  }

  public void stop() {
    this.position = 0;
  }

  public String getName() {
    return name;
  }

  public int getPosition() {
    return position;
  }
}
