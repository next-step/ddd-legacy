package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CarTest {
    @DisplayName("생성 조건을 확인한다.")
    @Test
    void constructor() {
       assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("자동차가 전진한다")
    @Test
    void go() {
        final Car car = new Car("frank");
        //조건에 대한 테스트가 주 관심사가 아닌, 전진하는 것이 주 관심사
        //그래서 협력하는 객체 ForwardStrategy를 사용한다.
        //그렇다면 조건에 대한 테스트와 전진의 test가 나눠지는것
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직이지 않는다")
    @Test
    void stop() {
        final Car car = new Car("frank");
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}