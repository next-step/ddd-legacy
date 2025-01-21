package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class CarTest {

    @DisplayName("자동차 이름 5글자 초과 시, 에러 발생")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("thisistestcar", 0));
    }

    @DisplayName("랜덤 값이 4 이상인 경우, 자동차 이동")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @ParameterizedTest
    void move(final int condition) {
        final Car mycar = new Car("mycar", 0);
        mycar.move(condition);
        assertThat(mycar.getPosition()).isEqualTo(1);
    }

    @DisplayName("랜덤 값이 4 미만인 경우, 자동차 정지")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int condition) {
        final Car mycar = new Car("mycar", 0);
        mycar.move(condition);
        assertThat(mycar.getPosition()).isEqualTo(0);
    }
}
