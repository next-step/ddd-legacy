package kitchenpos.fakeClient;


import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {
    private static final String PROFANITY_TEXT = "바보";

    public FakePurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    public boolean containsProfanity(final String text) {
        return (text.contains(PROFANITY_TEXT) ? true : false);
    }
}
