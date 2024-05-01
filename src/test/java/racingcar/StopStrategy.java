package racingcar;

public class StopStrategy implements MovingStrategy {
  @Override
  public boolean movable(int condition) {
    if (condition < 4) {
      return false;
    }

    throw new IllegalArgumentException();
  }
}
