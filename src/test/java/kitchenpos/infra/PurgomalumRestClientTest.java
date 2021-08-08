package kitchenpos.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@RestClientTest(PurgomalumClient.class)
class PurgomalumRestClientTest {

    @Autowired
    private PurgomalumClient service;

    @Autowired
    private MockRestServiceServer server;

    @DisplayName("영어 욕을 필터링한다")
    @ParameterizedTest
    @ValueSource(strings = {"fuck", "shit", "damn", "normal"})
    void containsProfanity(final String profanityWord) {
        //given
        final String expectResult = "true";
        this.server.expect(requestTo("https://www.purgomalum.com/service/containsprofanity?text=" + profanityWord))
            .andRespond(withSuccess(expectResult, MediaType.TEXT_PLAIN));

        //when
        final boolean sut = this.service.containsProfanity(profanityWord);

        //then
        assertThat(sut).isTrue();
    }

    @DisplayName("정상 단어는 필터링되지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"chicken", "pizza", "hamburger"})
    void noProfanity(final String normalWord) {
        //given
        final String expectResult = "false";
        this.server.expect(requestTo("https://www.purgomalum.com/service/containsprofanity?text=" + normalWord))
            .andRespond(withSuccess(expectResult, MediaType.TEXT_PLAIN));

        //when
        final boolean sut = this.service.containsProfanity(normalWord);

        //then
        assertThat(sut).isFalse();
    }

    @DisplayName("한글 욕은 필터링되지 않는다")
    @ParameterizedTest
    @ValueSource(strings = {"개새끼", "씨발"})
    void profanityKorean(final String profanitykoreanWord) {
        //given
        final String expectResult = "false";
        this.server.expect(requestTo("https://www.purgomalum.com/service/containsprofanity?text=" + profanitykoreanWord))
            .andRespond(withSuccess(expectResult, MediaType.TEXT_PLAIN));

        //when
        final boolean sut = this.service.containsProfanity(profanitykoreanWord);

        //then
        assertThat(sut).isFalse();
    }

}
