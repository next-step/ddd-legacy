package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("가나다라마바사아"));
    }

    @DisplayName("자동차 이동 랜덤값 - 성공: MovingStrategy 가 true")
    @Test
    void goStategy() {
        final Car car = new Car("문병량");
        car.move(new GoStrategy());
    }

    @DisplayName("자동차 이동 랜덤값 - 성공: MovingStrategy 가 true")
    @Test
    void stopStrategy() {
        final Car car = new Car("문병량");
        car.move(new StopStrategy());
    }
}
