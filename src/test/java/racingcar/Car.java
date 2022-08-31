package racingcar;

import java.util.Objects;

public class Car {

  private static final int MAXMUM_NAME_LENGTH = 5;

  private final String name;
  private int position;

  public Car(final String name) {
    this(name, 0);
  }

  public Car(String name, int position) {
    if (Objects.isNull(name) || name.isBlank()) {
      throw new IllegalArgumentException("자동차의 이름은 비어 있을 수 없습니다.");
    }

    if (name.length() > MAXMUM_NAME_LENGTH) {
      throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
    }
    this.name = name;
    this.position = position;
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
