package racingcar.domain;

public class Car {
  private static final int MAX_OF_NAME = 5;

  private final String name;

  public Car(String name) {
    validateSizeOfName(name);
    this.name = name;
  }

  private void validateSizeOfName(String name) {
    if (name.length() > MAX_OF_NAME) {
      throw new IllegalArgumentException();
    }
  }
}
