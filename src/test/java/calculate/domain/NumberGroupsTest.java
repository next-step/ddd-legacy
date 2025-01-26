package calculate.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class NumberGroupsTest {

    @ParameterizedTest
    @ValueSource(strings = {"1,2,3"})
    void 문자열_numbers_숫자_3개_생성_테스트(final String text) {
        NumberGroups numberGroups = new NumberGroups(text);

        assertAll(
                () -> assertThat(numberGroups).isEqualTo(new NumberGroups(new int[]{1, 2, 3})),
                () -> assertThat(numberGroups.sum()).isEqualTo(6)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void 문자열_numbers_2개_생성_테스트(final String text) {
        NumberGroups numberGroups = new NumberGroups(text);

        assertAll(
                () -> assertThat(numberGroups).isEqualTo(new NumberGroups(new int[]{1, 2})),
                () -> assertThat(numberGroups.sum()).isEqualTo(3)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void 문자열_커스텀_구분자_성공_테스트(final String text) {
        NumberGroups numberGroups = new NumberGroups(text);

        assertAll(
                () -> assertThat(numberGroups).isEqualTo(new NumberGroups(new int[]{1,2,3})),
                () -> assertThat(numberGroups.sum()).isEqualTo(6)
        );

    }

}