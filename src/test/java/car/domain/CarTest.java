package car.domain;

import car.strategy.MoveStrategyImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @Test
    void 자동차_생성_성공_테스트() {
        final Car car = Car.of("goJad", 0);

        assertThat(car.getCarName()).isEqualTo("goJad");
        assertThat(car.getMoveNumber()).isEqualTo(0);
    }

    @Test
    void 자동차_생성_이름_길이_5글자_초과_실패_테스트() {
        assertThatThrownBy(() -> Car.of("goJade", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름은 5글자 초과일 수 없습니다.");
    }

    @Test
    void 자동차_이동_초기_숫자__음수_실패_테스트() {
        assertThatThrownBy(() -> Car.of("goJad", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("0보다 큰 숫자를 입력해야합니다.");
    }

    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @ParameterizedTest
    void 숫자가_4이상_한칸_이동_테스트(int value) {
        final Car car = Car.of("goJad", 0);

        assertThat(car.move(value, MoveStrategyImpl.of(4))).isEqualTo(Car.of("goJad", 1));
    }

    @ValueSource(ints = {1, 2, 3})
    @ParameterizedTest
    void 숫자가_3이하_제자리_이동_테스트(int value) {
        final Car car = Car.of("goJad", 0);

        assertThat(car.move(value, MoveStrategyImpl.of(4))).isEqualTo(Car.of("goJad", 0));
    }

}
