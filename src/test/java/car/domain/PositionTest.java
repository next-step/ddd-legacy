package car.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositionTest {

    @Test
    void 차량_움직인_횟수_초기화_생성_성공_테스트() {
        final Position position = new Position(0);

        assertThat(position.number()).isEqualTo(0);

    }

    @Test
    void 차량_움직인_횟수_초기화_생성_실패_테스트() {
        assertThatThrownBy(() -> new Position(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("0보다 큰 숫자를 입력해야합니다.");
    }

    @Test
    void 숫자가_4이상_이면_한칸_이동_테스트() {
        final Position position = new Position(0);

        assertThat(position.move(true)).isEqualTo(new Position(1));

    }

    @Test
    void 숫자가_3이하_이면_제자리_이동_테스트() {
        final Position position = new Position(0);

        assertThat(position.move(false)).isEqualTo(new Position(0));
    }

}
