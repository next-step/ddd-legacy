package calculator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PositiveNumberTest {

    @DisplayName("음수입력시 에러가 발생한다.")
    @Test
    void 음수입력불가능() {
        assertThatThrownBy(() -> new PositiveNumber("-1"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("한글입력시 에러가 발생한다.")
    @Test
    void 한글입력불가능() {
        assertThatThrownBy(() -> new PositiveNumber("가"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("영어입력시 에러가 발생한다.")
    @Test
    void 영어입력불가능() {
        assertThatThrownBy(() -> new PositiveNumber("abc"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("값 조회시 생성자에서 입력한 값이 들어있어야 한다.")
    @Test
    void 값검증() {
        Assertions.assertAll(
                () -> assertThat(new PositiveNumber("1").value()).isEqualTo(1),
                () -> assertThat(new PositiveNumber("3").value()).isEqualTo(3),
                () -> assertThat(new PositiveNumber("5").value()).isEqualTo(5)
        );
    }
}
