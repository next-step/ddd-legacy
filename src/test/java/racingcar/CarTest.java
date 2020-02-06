package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {
    @Test
    @DisplayName("자동차를 생성한다.")
    void create() {
        //new Car("nahee") 이렇게 해도 junit에서 해줌
        Car car = new Car("nahee");
        assertThat(car).isNotNull();
    }

    @Test
    @DisplayName("다섯글자가 넘는 이름으로는 자동차가 생성되지 않는다.")
    void createExceptionOverFiveLetters() {
        assertThrows(IllegalArgumentException.class, () -> new Car("naheenosaur"));
    }

    @Test
    @DisplayName("자동차가 움직이는지 확인한다 - interface 추출")
    void moveWithInterface() {
        Car car = new Car("nahee");

        class move implements MovingStrategy {
            @Override
            public boolean movable() {
                return true;
            }
        }

        car.move(new move());
        assertThat(car.getPosition()).isEqualTo(1);

        class stop implements MovingStrategy {
            @Override
            public boolean movable() {
                return false;
            }
        }

        car.move(new stop());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차가 움직이는지 확인한다 - 익명클래스 사용")
    void moveWithAnonymous() {
        Car car = new Car("nahee");
        RandomMovingStrategy move = new RandomMovingStrategy() {
            @Override
            public boolean movable() {
                return true;
            }
        };
        car.move(move);
        assertThat(car.getPosition()).isEqualTo(1);

        RandomMovingStrategy stop = new RandomMovingStrategy() {
            @Override
            public boolean movable() {
                return false;
            }
        };
        car.move(stop);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차가 움직이는지 확인한다 - lambda")
    void moveWithLambda() {
        Car car = new Car("nahee");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);

        car.move(() -> false);
        assertThat(car.getPosition()).isEqualTo(1);
    }
}