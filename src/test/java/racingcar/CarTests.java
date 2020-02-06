package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTests {

    @DisplayName("이름이 다섯 글자 이하인 자동차 객체 생성 시도 - 성공")
    @ParameterizedTest
    @ValueSource(strings = {"1", "22", "333", "4444", "55555"})
    void tryCreateCarInHappyPath(String name) {
        // when
        final Car car = new Car(name);

        // then
        assertThat(car.getName()).isNotNull();
    }

    @DisplayName("이름이 다섯 글자 초과하는 자동차 객체 생성 시도 - 실패")
    @ParameterizedTest
    @ValueSource(strings = {"willFail", "tooLong"})
    void tryCreateCarInErrorCase(String name) {
        // then
        assertThatIllegalArgumentException().isThrownBy(() -> {
            new Car(name);        //when
        });
    }

    @DisplayName("자동차 움직이기 시도 - 성공")
    @Test
    void tryMoveCarInHappyPath() {
        // when
        Car car = new Car("test");
        car.move(() -> true);       // java8의 functional method 사용

        // then
        assertThat(car.getPosition()).isEqualTo(1);
    }
}
