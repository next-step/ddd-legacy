package kitchenpos.mock.client;

import kitchenpos.infra.client.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {

    private static final String BAD_WORD = "bad";

    public FakePurgomalumClient(
        RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    public boolean containsProfanity(final String text) {
        return text.contains(BAD_WORD);
    }
}
