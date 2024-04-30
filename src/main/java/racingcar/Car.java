package racingcar;

import org.springframework.util.Assert;

public record Car (
        String name,
        int position
){

    public static final int DISTANCE_MIN = 4;
    public static final int CAR_NAME_LENGTH_MAX = 5;

    public Car {
        Assert.isTrue(name.length() <= CAR_NAME_LENGTH_MAX, "자동차 이름은 5글자를 넘을 수 없습니다.");
    }

    public Car move(MoveStrategy strategy) {
        var distance = strategy.getDistance();
        if (distance < DISTANCE_MIN) {
            return this;
        }
        return new Car(this.name, this.position + distance);
    }

    public Car stop() {
        return this;
    }
}
