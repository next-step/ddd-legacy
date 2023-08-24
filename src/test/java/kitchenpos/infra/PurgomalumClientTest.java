package kitchenpos.infra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PurgomalumClientTest {

    @Autowired
    PurgomalumClient purgomalumClient;

    @ParameterizedTest
    @CsvSource(value = {"fuck", "thisShitOnlyCheckEnglish"})
    @DisplayName("욕설 확인 실 테스트")
    void name(String profanity) {
        assertThat(purgomalumClient.containsProfanity(profanity)).isTrue();
    }
}