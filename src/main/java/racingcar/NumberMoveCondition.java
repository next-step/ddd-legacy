package racingcar;

public class NumberMoveCondition implements MoveCondition {

  final int condition;

  public NumberMoveCondition(int condition) {
    this.condition = condition;
  }

  @Override
  public boolean movable() {
    return condition >= 4;
  }
}
