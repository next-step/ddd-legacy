package racingcar;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {


    @DisplayName("자동차는 이름을 가지고 있다.")
    @Test
    void name(){
        final var actual = new Car("yeol");
        assertThat(actual.getName()).isEqualTo("yeol");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다.")
    @Test
    void invalid_name(){
        assertThatThrownBy(()->new Car("가나다라마바사아"))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다.")
    @Test
    void move(){
        final Car car = new Car("yeol");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다.")
    @Test
    void stop(){
        final Car car = new Car("yeol");
        car.move(new NumberMoveCondition(3));
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
