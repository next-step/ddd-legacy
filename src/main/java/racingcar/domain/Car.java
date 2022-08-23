package racingcar.domain;

import racingcar.strategy.MoveStrategy;

public class Car {
  private static final int MAX_OF_NAME = 5;

  private final String name;
  private int distance;

  public Car(String name) {
    validateSizeOfName(name);
    this.name = name;
    this.distance = 0;
  }

  private void validateSizeOfName(String name) {
    if (name.length() > MAX_OF_NAME) {
      throw new IllegalArgumentException();
    }
  }

  public void move(MoveStrategy moveStrategy) {
    if (moveStrategy.canMove()) {
      distance++;
    }
  }

  public int getDistance() {
    return distance;
  }
}
