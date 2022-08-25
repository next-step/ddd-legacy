package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 * JUnit 5의 @DisplayName을 사용하여 테스트 메서드의 의도를 한글로 표현한다.
 * JUnit의 Assertions이 아닌 AssertJ의 Assertions를 사용한다.
 * 개발자는 코드를 작성하면서 확장성(scalability)에 대한 고민을 항상 해야 한다. 자동차가 다양한 방법으로 움직일 수 있게 구현한다.
 * 자동차가 조건에 따라 움직였는지 움직이지 않았는지 테스트하고자 할 때는 isBetween(), isGreaterThan(), isLessThan() 등은 사용하지 않는다.
 */
public class CarTest {
    @DisplayName("자동차 이름은 5글자보다 작거나 같다.")
    @ParameterizedTest
    @ValueSource(strings = {"wenoo", "weno"})
    void construct(String name) {
        Car car = new Car(name);

        assertThat(car.getName()).isEqualTo(name);
    }

    @DisplayName("자동차 이름은 5글자보다 클 수 없다.")
    @Test
    void construct_with_greater_than_5_letters() {
        String name = "wenoaa";

        assertThatIllegalArgumentException().isThrownBy(
                () -> new Car(name)
        );
    }

    @DisplayName("자동차 이름은 비어있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void construct_with_empty_name(String name) {
        assertThatIllegalArgumentException().isThrownBy(
                () -> new Car(name)
        );
    }

    @DisplayName("자동차는 이름과 위치값을 가질 수 있다.")
    @Test
    void construct_with_name_and_position() {
        String name = "weno";
        int position = 5;
        Car car = new Car(name, position);

        assertThat(car.getName()).isEqualTo(name);
        assertThat(car.getPosition()).isEqualTo(position);
    }

    @DisplayName("자동차 이름만 추가하면 위치값은 0으로 설정된다.")
    @Test
    void construct_with_only_name_and_zero_position() {
        String name = "weno";
        Car car = new Car(name);

        assertThat(car.getPosition()).isEqualTo(0);
    }
}
