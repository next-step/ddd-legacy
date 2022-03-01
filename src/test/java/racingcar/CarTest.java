package racingcar;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("동해물과백두산이", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다")
    @Test
    void go() {
        final Car car = new Car("keun");
//        car.move(() -> true); // 1. 람다를 사용
//        car.move(new MovingStrategy() { // 2. 인터페이스 구현을 사용
//            @Override
//            public boolean movable() {
//                return false;
//            }
//        });
//        car.move(new RandomMovingStrategy()); // 요구사항은 아니지만 랜덤으로 확인하는 방법
        car.move(new ForwardStrategy()); // 람다를 사용하는 방법
        assertThat(car.getPosition()).isEqualTo(1);
    }


    // @Disabled // 사용하지 않는 테스트 코드 막기
//    @ValueSource(ints = {0, 1, 2, 3})
//    @ParameterizedTest
    @DisplayName("자동차가 움직이지 않는다")
    @Test
    void hold(final int number) {
        final Car car = new Car("keun", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }
    
}