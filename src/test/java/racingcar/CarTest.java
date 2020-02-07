package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

    @Test
    @DisplayName("자동차를 생성한다")
    void create(){
        final Car car = new Car("TEST");
        assertThat(car).isNotNull();
    }

    @Test
    @DisplayName("자동차 이름 예외처리")
    void validateByNameLength(){
        String longName = "ABCDEF";

        assertThat(longName.length()).isGreaterThan(5);
        assertThatIllegalArgumentException().isThrownBy(() -> {
            new Car(longName);
        });
    }

    @Test
    @DisplayName("자동차 이동 유무 처리")
    void move(){
        Car car = new Car("test");

        car.move(()->false);
        assertThat(car.getPosition()).isEqualTo(0);

        car.move(()->true);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}
