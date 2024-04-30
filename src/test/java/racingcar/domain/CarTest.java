package racingcar.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarTest {
    @Nested
    class moveTest {
        private Car car;

        @BeforeEach
        void setUp() {
            car = new Car("marco");
        }

        @DisplayName("move 메서드에서 MoveStrategy가 true를 반환하면 position이 1 증가한다.")
        @Test
        void moveOfReturnTrueMoveStrategyTest() {
            car.move(() -> true);
            assertThat(car.getPosition()).isEqualTo(1);
        }

        @DisplayName("move 메서드에서 MoveStrategy가 false를 반환하면 position이 증가하지 않는다.")
        @Test
        void moveOfReturnFalseMoveStrategyTest() {
            car.move(() -> false);
            assertThat(car.getPosition()).isZero();
        }
    }
}
