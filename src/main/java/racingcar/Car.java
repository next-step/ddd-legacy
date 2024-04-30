package racingcar;

import org.springframework.util.Assert;

public record Car (
        String name,
        int position
){
    public Car {
        Assert.isTrue(name.length() <= 5, "자동차 이름은 5글자를 넘을 수 없습니다.");
    }

    public Car move(int distance) {
        if (distance < 4) {
            return this;
        }
        return new Car(this.name, this.position + distance);
    }
}
