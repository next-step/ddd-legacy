package racingcar;

import org.springframework.util.StringUtils;

public class Car {
    public Car(String name) {
        if (name == null || name.length() <= 0) {
            throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
        }
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없다.");
        }


    }
}
