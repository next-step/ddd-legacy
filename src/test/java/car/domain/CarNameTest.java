package car.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarNameTest {

    @Test
    void 자동차_이름_생성_성공_테스트() {
        final CarName carName = CarName.from("ddCar");
        assertThat(carName.getName()).isEqualTo("ddCar");
    }

    @Test
    void 자동차_이름_생성_실패_테스트() {
        assertThatThrownBy(() -> CarName.from("kiaCarJade"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름은 5글자 초과일 수 없습니다.");

    }

}
