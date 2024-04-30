package kitchenpos.racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    @DisplayName("자동차 이름이 5글자 초과일 때 예외를 발생시킨다.")
    @Test
    void throwsExceptionWhenCarNameExceedsLengthLimit() {
        String name = "abcdef";

        Assertions.assertThatThrownBy(() -> new Car(name))
                  .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("전진 값이 4미만일 경우에는 자동차는 이동하지 않는다.")
    @Test
    void doesNotMoveCarWhenConditionIsNotMet() {
        Car car = new Car("car");

        car.move(() -> false);

        Assertions.assertThat(car.getPosition()).isEqualTo(0);
    }

    @DisplayName("전진 값이 4이상일 경우에는 자동차가 한칸 이동한다.")
    @Test
    void movesCarWhenConditionIsMet() {
        Car car = new Car("car");

        car.move(() -> true);

        Assertions.assertThat(car.getPosition()).isEqualTo(1);
    }
}
