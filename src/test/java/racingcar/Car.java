package racingcar;

public class Car {
  private final String name;
  private int position;

  public Car(String name, int position) {
    if (name.length() > 5) {
      throw new IllegalArgumentException();
    }

    this.name = name;
    this.position = position;
  }

  public boolean isMoving(MovingStrategy movingStrategy, int condition) {
    if (movingStrategy.movable(condition)) {
      this.position++;
      return true;
    } else {
      return false;
    }
  }
}
