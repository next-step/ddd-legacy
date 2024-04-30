package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Car {
    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.position = position;
    }

    public void move(MoveStrategy moveStrategy) {
        if (moveStrategy.movable()) {
            position++;
        }
    }

    public int position() {
        return position;
    }
}

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("abcdef"));
    }

    @DisplayName("숫자가 4 이상인 경우 자동차가 전진한다")
    @Test
    void move() {
        Car car = new Car("abcd");
        car.move(new GoStrategy());
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만인 경우 자동차가 정지한다")
    @Test
    void stop() {
        Car car = new Car("abcd");
        car.move(new StopStrategy());
        assertThat(car.position()).isEqualTo(0);
    }
}
