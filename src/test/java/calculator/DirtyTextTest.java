package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class DirtyTextTest {

    private Refiner refiner;

    @BeforeEach
    void setUp() {
        refiner = new FakeRefiner();
    }

    @DisplayName("유효하지 않은 생성 파라미터를 전달하는 경우 예외를 발생시킨다.")
    @Test
    void constructor() {
        // given

        // when & then
        SoftAssertions.assertSoftly(softly -> {

            // null refiner
            softly.assertThatThrownBy(() -> new DirtyText(null, null))
                .isInstanceOf(IllegalArgumentException.class);
        });
    }

    @DisplayName("value가 null이거나 빈 값이면 true를 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void isEmpty_true(final String value) {
        // given
        final DirtyText dirtyText = create(value);

        // when & then
        assertThat(dirtyText.isEmpty()).isTrue();
    }

    @DisplayName("value가 빈 값이 아니면 false 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"hello", "world"})
    void isEmpty_false(final String value) {
        // given
        final DirtyText dirtyText = create(value);

        // when & then
        assertThat(dirtyText.isEmpty()).isFalse();
    }


    @DisplayName("유효하지 않은 파라미터를 전달한 경우 예외를 발생시킨다")
    @ParameterizedTest
    @NullSource
    void refine_parameter(final String value) {
        // given
        final DirtyText dirtyText = create(value);

        // when & then
        assertThatThrownBy(dirtyText::refine)
            .isInstanceOf(IllegalArgumentException.class);
    }

    private DirtyText create(final String value) {
        return new DirtyText(value, refiner);
    }
}