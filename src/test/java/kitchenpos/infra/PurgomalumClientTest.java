package kitchenpos.infra;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.TestConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class PurgomalumClientTest {

    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper objectMapper;
    private PurgomalumClient purgomalumClient;

    public PurgomalumClientTest(final RestTemplateBuilder restTemplateBuilder, final ObjectMapper objectMapper) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    void setUp() {
        purgomalumClient = new PurgomalumClient(restTemplateBuilder, objectMapper);
    }

    @DisplayName("영어 욕을 필터링한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "shit", "nigga"})
    void containsProfanity(final String profanityWord) {
        final boolean sut = purgomalumClient.containsProfanity(profanityWord);
        assertThat(sut).isTrue();
    }

    @DisplayName("정상 단어는 필터링되지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"후라이드", "양념", "치킨"})
    void noProfanity(final String normalWord) {
        final boolean sut = purgomalumClient.containsProfanity(normalWord);
        assertThat(sut).isFalse();
    }

    @DisplayName("한글 욕은 필터링되지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"개새끼", "씨발"})
    void profanityKorean(final String profanitykoreanWord) {
        final boolean sut = purgomalumClient.containsProfanity(profanitykoreanWord);
        assertThat(sut).isFalse();
    }

}
