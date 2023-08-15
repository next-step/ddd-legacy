package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CarTest {

    @DisplayName("자동차 이름")
    @Nested
    class Name {
        @DisplayName("[성공] 자동차 이름이 5글자 이상이어야 한다.")
        @ParameterizedTest
        @ValueSource(strings = {"55555", "66666", "가나라다마사아자차카"})
        void carNameLongerThanFive(String input) {
            assertDoesNotThrow(() -> new Car(input));
        }

        @DisplayName("[실패] 자동차 이름이 5글자 미만이면 안된다.")
        @ParameterizedTest
        @ValueSource(strings = {"1", "22", "333", "4444"})
        void carNameShorterThanFive(String input) {
            assertThatThrownBy(() -> new Car(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[실패] 자동차 이름이 공백이거나 null일 수 없다.")
        @ParameterizedTest
        @NullAndEmptySource
        void carNameNullAndEmpty(String input) {
            assertThatThrownBy(() -> new Car(input))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("자동차 이동")
    @Nested
    class Move {
        @DisplayName("[성공] 랜덤 값이 4이상이면 이동한다.")
        @ParameterizedTest
        @ValueSource(ints = {4,5,6,7,8,9})
        void carMoveWhenBiggerThanFour(int input){
            //given
            Car car = new Car("name", new FixMoveStrategy(input));
            //when
            car.move();
            //then
            assertThat(car.getPosition).isEqualTo(1);
        }

        @DisplayName("[성공] 랜덤 값이 4미만이면 이동하지 않는다.")
        @ParameterizedTest
        @ValueSource(ints = {0,1,2,3})
        void carMoveWhenBiggerThanFour(int input){
            //given
            Car car = new Car("name", new FixMoveStrategy(input));
            //when
            car.move();
            //then
            assertThat(car.getPosition).isEqualTo(0);
        }
    }

}