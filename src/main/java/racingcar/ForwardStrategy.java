package racingcar;

public class ForwardStrategy implements MovingStrategy {
    @Override
    public boolean isMovable(int position) {
        return (position > 4);
    }


}
