package racingcar;

public class TestMovingStrategy {
    public class MovedStrategy implements MovingStrategy {
        @Override
        public boolean movable() {
            return true;
        }
    }

    public class NotMovedStrategy implements MovingStrategy {
        @Override
        public boolean movable() {
            return false;
        }
    }
}
