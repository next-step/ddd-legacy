package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자 이하이다")
    void nameTest01() {
        assertDoesNotThrow(() -> {
            new Car("hello");
        });
    }

    @Test
    @DisplayName("자동차 이름이 5글자를 넘으면 IllegalArgumentException 이 발생한다")
    void nameTest02() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Car("hello2");
        });
    }

    @Test
    @DisplayName("자동차 이름은 길이가 0일 수 있다")
    void nameTest03() {
        assertDoesNotThrow(() -> {
            new Car("");
        });
    }

    @Test
    @DisplayName("자동차 이름은 null 이면 NullPointerException 이 발생한다")
    void nameTest04() {
        assertThrows(NullPointerException.class, () -> {
            new Car(null);
        });
    }

    @Test
    @DisplayName("자동차 이름을 확인할 수 있다")
    void nameTest05() {
        final String name = "안녕하세요";
        Car car = new Car(name);
        assertThat(car.getName()).isEqualTo(name);
    }

    @DisplayName("MovingStrategy 가 참을 반환하면 자동차는 움직일 수 있다")
    @Test
    void moveTest01() {
        // given
        MovingStrategy strategy = mock(MovingStrategy.class);
        Car car = new Car("hello", strategy);
        given(strategy.moveAble()).willReturn(true);

        // when, then
        assertThat(car.move()).isTrue();
    }

    @DisplayName("MovingStrategy 가 거짓을 반환하면 자동차는 움직일 수 없다")
    @Test
    void moveTest02() {
        // given
        MovingStrategy strategy = mock(MovingStrategy.class);
        Car car = new Car("hello", strategy);
        given(strategy.moveAble()).willReturn(false);

        // when, then
        assertThat(car.move()).isFalse();
    }
}
