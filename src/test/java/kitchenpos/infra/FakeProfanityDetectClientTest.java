package kitchenpos.infra;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FakeProfanityDetectClientTest {

    // SUT

    private final FakeProfanityDetectClient fakeProfanityDetectClient = new FakeProfanityDetectClient();

    @DisplayName("비속어이거나 비속어를 포함하면 True를 반환해야 한다.")
    @ValueSource(strings = {
        "holiday", "bed", "anxious", "everyday", "reach",
        "private holiday", "courage bed", "about anxious", "knee everyday", "number reach",
        "holiday roll", "bed avenue", "anxious needle", "everyday chairman", "reach cape",
        "interrupt holiday sauce", "explore bed radio", "return anxious motherly",
        "entertain everyday cause", "separate reach they",
    })
    @ParameterizedTest
    void ejeeflhp(final String text) {
        // when
        final boolean result = this.fakeProfanityDetectClient.containsProfanity(text);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("비속어를 포함하지 않으면 False를 반환해야 한다.")
    @ValueSource(strings = {
        "escape", "permission", "stomach", "manner", "opposition",
        "social furnish", "direction rock", "thread feather", "rice sea", "lock photography",
    })
    @ParameterizedTest
    void flqcauff(final String text) {
        // when
        final boolean result = this.fakeProfanityDetectClient.containsProfanity(text);

        // then
        assertThat(result).isFalse();
    }
}
