package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    void carNameTest(){
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("abcedfhgi"));
    }

    @Test
    void move(){
        final var car = new Car("홍길동");
        car.move(4);
        assertThat(car.position()).isEqualTo(1);
    }

    @Test
    void stop(){
        final var car = new Car("홍길동");
        car.move(3);
        assertThat(car.position()).isEqualTo(0);
    }
}
