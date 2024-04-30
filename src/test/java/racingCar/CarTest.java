package racingCar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class Car {
    private final String name;
    private int position;

    public Car(String name, int position) {
        if (name.length() > 5){
            throw new IllegalArgumentException("이름은 5글자를 넘을 수 없다.");
        }
        this.name = name;
        this.position = position;
    }

    public Car(String name) {
        this(name, 0);
    }

    public void move(int condition) {
        if (condition >= 4){
            position++;
        }
    }

    public int Position() {
        return position;
    }
}

public class CarTest {
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @DisplayName("숫지가 4이상인 경우 자통차는 전진한다. ")
    @Test
    void move() {
        final var car = new Car("홍길동");
        car.move(4);
        assertThat(car.Position()).isEqualTo(1);
    }

    @DisplayName("숫자가 4미만인 경우 자동차는 움직이지 않는다.")
    @Test
    void stop() {
        final var car = new Car("홍길동");
        car.move(3);
        assertThat(car.Position()).isEqualTo(0);
    }


}
