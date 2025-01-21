package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("자동차 테스트")
public class CarTest {

    @DisplayName("자동차 이름은 5글자를 초과할 수 없다")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("123456"));
    }


    @DisplayName("자동차는 조건이 4이상일 경우 1칸 움직일 수 있다.")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9, 10})
    @ParameterizedTest
    void move(final int condition) {
        final Car car = new Car("woozi");
        car.move(condition);

        assertThat(car.position()).isEqualTo(1);
    }
}
