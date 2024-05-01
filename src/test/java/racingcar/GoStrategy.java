package racingcar;

public class GoStrategy implements MovingStrategy {
  @Override
  public boolean movable(int condition) {
    if (condition > 4) {
      return true;
    }

    throw new IllegalArgumentException();
  }
}
