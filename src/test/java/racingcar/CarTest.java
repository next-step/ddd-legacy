package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    void 자동차_이름은_5글자를_넘을_수_없다() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과백두산이"));
    }

    @Test
    void 숫자가_4이상인_경우_자동차는_전진한다() {
        final Car car = new Car("홍길동");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    void 숫자가_4미만인_경우_자동차는_움직이지_않는다() {
        final Car car = new Car("홍길동");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
