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
    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다.")
    void move(){
        final var car = new Car("홍길동");
        car.move(new GoStrategy());
        assertThat(car.position()).isEqualTo(1);
    }

    @Test
    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다.")
    void stop(){
        final var car = new Car("홍길동");
        car.move(new StopStrategy());
        assertThat(car.position()).isEqualTo(0);
    }
}
