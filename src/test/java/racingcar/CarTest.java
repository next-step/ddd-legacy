package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @Test
    @DisplayName("자동차의 이름은 5글자를 넘길 수 없다")
    void 자동차의_이름은_5글자를_넘길_수_없다() {
        assertThatThrownBy(() -> {
            Car car = new Car("메르세데스 벤츠");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("값이 4이상이면 자동차가 한칸 움직인다")
    void 값이_4이상이면_자동차가_한칸_움직인다() {
        Car 람보르기니 = new Car("람보르기니", 5);

        람보르기니.move(new RandomMovingCondition(), 4);

        assertThat(람보르기니.getPosition()).isEqualTo(6);
    }

    @Test
    @DisplayName("값이 4미만 이면 자동차는 움직이지 않는다")
    void 값이_4미만이면_자동차는_움직이지_않는다() {
        Car 람보르기니 = new Car("람보르기니", 5);

        람보르기니.move(new RandomMovingCondition(), 3);

        assertThat(람보르기니.getPosition()).isEqualTo(5);
    }
}
