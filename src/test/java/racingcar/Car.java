package racingcar;

import java.util.Objects;

/**
 * racingcar 패키지의 Car에 대한 테스트 코드를 작성하며 JUnit 5에 대해 학습한다.
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 */
public class Car {
    private final CarName name;

    public Car(String name) {
        this.name = CarName.of(name);
    }

    public String getName() {
        return name.getValue();
    }
}
