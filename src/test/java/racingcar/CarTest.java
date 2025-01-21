package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

    @DisplayName("자동차 이름 5글자 초과 시, 에러 발생")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("thisistestcar", 0));
    }

    @DisplayName("자동차 이동")
    @Test
    void move() {
        final Car mycar = new Car("mycar", 0);
//        mycar.move(() -> true);
        mycar.move(new ForwardStrategy());
        assertThat(mycar.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차 정지")
    @Test
    void stop() {
        final Car mycar = new Car("mycar", 0);
//        mycar.move(() -> false);
        mycar.move(new StopStrategy());
        assertThat(mycar.getPosition()).isEqualTo(0);
    }
}
