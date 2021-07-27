package study;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("동해물과 백두산이"));
    }

    @DisplayName("값이 4 이상인 경우 자동차가 이동한다.")
    @Test
    void move() {
        final Car car = new Car("Solar");
        car.move(4);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("값이 4 이상인 경우 자동차가 정한다.")
    @Test
    void not_move() {
        final Car car = new Car("Solar");
        car.move(3);
        assertThat(car.getPosition()).isEqualTo(0);
    }

    @Test
    void name() {
        final Car car = new Car("Solar");
        //car.move(new RandomMovingStrategy()); //DI
        // 테스트해야할 것은 moable() 결과값에 따라 car가 이동하느냐 안하느냐만 테스트하면 된다.
        // 실제 코드에서 사용하는 구현체는 RandomMovingStrategy이다. 이 구현체는 변경될 수 있다.
        // 실제 객체로는 랜덤한 값이 나오기 때문에 테스트하기 어렵다.
        // MovingStrategy의 가짜 객체를 넣어서 테스트한다.
        // 1.람다 사용
        car.move(new MovingStrategy() {
            @Override
            public boolean movable() {
                return true;
            }
        });
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    void move_2() {
        final Car car = new Car("Solar");
        // 2. 자주 사용되면 fake 오브젝트를 만들어준다.
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    void not_move_2() {
        final Car car = new Car("Solar");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
