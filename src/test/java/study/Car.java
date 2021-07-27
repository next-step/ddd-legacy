package study;

public class Car {

  private final String name;
  private int position;

  public Car(String name) {
    if (name.length() > 5) {
      throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
    }
    this.name = name;
    this.position = 0;
  }

  public void move(final MovingStrategy movingStrategy) {
    if (movingStrategy.movable()) {
      this.position++;
    }
  }

  public int getPosition() {
    return position;
  }
}
