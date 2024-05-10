package racingcar;

import racingcar.strategy.MovingStrategy;

class Car {
  public static final int NAME_LENGTH_LIMIT = 5;
  public static final int MOVING_CONDITION = 4;
  public static final int MOVING_POSITION_STEP = 1;
  private final String name;
  private int position;

  private Car(final String name, final int position) {
    this.name = name;
    this.position = position;
  }

  public static Car createCar(final String name, final int position) {
    validateCarName(name);

    return new Car(name, position);
  }

  private static void validateCarName(final String name) {
    if (name.length() > NAME_LENGTH_LIMIT) {
      throw new IllegalArgumentException(String.format("이름의 글자수가 %d 를 넘었습니다.", NAME_LENGTH_LIMIT));
    }
  }

  public void move(final MovingStrategy movingStrategy) {
    if (movingStrategy.movable()) {
      this.position += MOVING_POSITION_STEP;
    }
  }

  public int getPosition() {
    return position;
  }
}
