package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarNameTest {

    @Test
    @DisplayName("차 이름은 입력값 그대로 사용한다")
    void 차_이름_생성() {
        final String name = "레진코믹스";

        final CarName carName = new CarName(name);

        assert carName.name().equals(name);
    }

    @ParameterizedTest
    @DisplayName("차는 1~5 글자의 이름을 가질 수 있다")
    @ValueSource(strings = {"레", "레진", "레진코", "레진코믹", "레진코믹스"})
    void 차_이름_생성_성공(final String name) {

        assertThatNoException()
                .isThrownBy(
                        () -> new CarName(name)
                );
    }

    @ParameterizedTest
    @DisplayName("차 이름 생성 실패시 IllegalArgumentException 발생한다")
    @ValueSource(strings = {"", "레진코믹스고"})
    void 차_이름_생성_실패(final String name) {

        assertThatThrownBy(() -> new CarName(name))
                .isInstanceOf(IllegalArgumentException.class);
    }
}