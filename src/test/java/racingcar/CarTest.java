package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Car {
    private String name;
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

    public void move(int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public int position() {
        return position;
    }
}

class CarTest {
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void validCarNameTest() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("abcdefg"));
    }

    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다.")
    @Test
    void moveTest() {
        final Car car = new Car("보라돌");
        car.move(4);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다.")
    @Test
    void stopTest() {
        final Car car = new Car("보라돌");
        car.move(3);
        assertThat(car.position()).isEqualTo(0);
    }
}
