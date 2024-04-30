package racingcar;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("자동차(Car) 단위 테스트")
class CarTest {

    @DisplayName("자동차 이름과 자동차 위치로 자동차를 생성한다.")
    @Test
    void carInitTest() {
        // when
        var car = new Car("소나타V5", 0);

        // then
        SoftAssertions.assertSoftly(softly-> {
            softly.assertThat(car.name()).isEqualTo("소나타V5");
            softly.assertThat(car.position()).isEqualTo(0);
        });
    }

    @DisplayName("자동차 이름은 5글자를 넘을 경우 예외 발생한다.")
    @Test
    void carNameMaxLengthTest() {
        // when & then
        Assertions.assertThatThrownBy(() -> new Car("소나타V51", 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 움직임 값이 4 이상이면 움직인다.")
    @Test
    void carMoveMinTest() {
        // given
        var car = new Car("소나타V5", 0);

        // when
        var actual = car.move(4);

        // expected
        var expected = new Car("소나타V5", 4);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("자동차는 움직임 값이 4 미만이면 움직이지 않는다.")
    @Test
    void carDontMoveTest() {
        // given
        var car = new Car("소나타V5", 0);

        // when
        var actual = car.move(3);

        // expected
        var expected = new Car("소나타V5", 0);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("자동차는 앞으로 움직인다.")
    @Test
    void carMoveMoveForwardTest() {

    }


    @DisplayName("자동차는 뒤로 움직인다.")
    @Test
    void carMoveMoveBackwardTest() {

    }

}
