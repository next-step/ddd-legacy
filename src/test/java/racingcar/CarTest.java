package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//자동차 이름은 5 글자를 넘을 수 없다.
//5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
//자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
class CarTest {
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    @Test
    public void constructor() throws Exception {
        //given


        //when


        //then
        assertThatThrownBy(() -> new Car("동해물과백두산이", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다")
    @Test
    public void forward() throws Exception {
        //given
        final Car car = new Car("sws", 0);

        //when
        car.move(new ForwardStrategy());

        //then
        assertThat(car.getPosition()).isOne();
    }

    @DisplayName("자동차가 움직이지 않는다")
    @Test
    public void hold() throws Exception {
        //given
        final Car car = new Car("sws", 0);

        //when
        car.move(new HoldStrategy());

        //then
        assertThat(car.getPosition()).isZero();
    }
}