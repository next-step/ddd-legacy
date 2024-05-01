package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("자동차 도메인 테스트")
@ExtendWith(MockitoExtension.class)
public class CarTest {
    @Mock
    MoveStrategy fakeMoveStrategy;

    @InjectMocks
    Car fakeCar = new Car();

    @Test
    @DisplayName("자동차의 이름은 5글자를 넘을 수 없다.")
    void handleNameLength() {
        String name = "abcdef";
        Assertions.assertThatThrownBy(
                () -> new Car(name)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("값이 4 이상일 경우 자동차가 움직인다.")
    void move() {
        int randomValue = 4;

        Mockito.when(fakeMoveStrategy.isMovable(randomValue))
                .thenReturn(true);

        Assertions.assertThat(fakeCar.movable(fakeMoveStrategy, randomValue))
                .isEqualTo(true);
    }

    @Test
    @DisplayName("값이 4 미만일 경우 자동차는 움직이지 않는다.")
    void stop() {
        int randomValue = 3;

        Mockito.when(fakeMoveStrategy.isMovable(randomValue))
                .thenReturn(false);

        Assertions.assertThat(fakeCar.movable(fakeMoveStrategy, randomValue))
                .isEqualTo(false);
    }

    class Car {
        private String name;
        private int position;

        public Car() {
            name = "";
            position = 0;
        }

        public Car(String name) {
            handleNameLength(name);
            this.name = name;
        }

        private void handleNameLength(String name) {
            if (5 < name.length()) {
                throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없습니다.");
            }
        }

        public boolean movable(MoveStrategy strategy, int value) {
            if (strategy.isMovable(value)) {
                position++;
                return true;
            }
            return false;
        }
    }

    interface MoveStrategy {
        boolean isMovable(int value);
    }
}