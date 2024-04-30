package racingcar;

import org.springframework.util.Assert;

public record Car (
        String name,
        int position
){
    public Car(String name, int position) {
        Assert.isTrue(name.length() <= 5, "자동차 이름은 5글자를 넘을 수 없습니다.");
        this.name = name;
        this.position = position;
    }
}
