package racingcar;

import java.util.Random;

public class Car {
  private String name;
  private int position;
  private int currentPosition;

  public Car(String name, int position) {
    if (name.length() > 5) {
      throw new IllegalArgumentException();
    }

    this.name = name;
    this.position = position;
    this.currentPosition = position;
  }

  public void setPosition(int random) {
    if (random > 4) {
      this.position++;
    }
  }

  public int getPosition() {
    return position;
  }

  public int getCurrentPosition() {
    return currentPosition;
  }

  public boolean isMoving() {
    if (this.position == this.currentPosition) {
      return false;
    }

    this.currentPosition = this.position;
    return true;
  }
}
