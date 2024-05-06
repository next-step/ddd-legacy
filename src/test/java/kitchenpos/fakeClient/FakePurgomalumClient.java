package kitchenpos.fakeClient;


import kitchenpos.infra.PurgomalumClient;
import org.springframework.boot.web.client.RestTemplateBuilder;

public class FakePurgomalumClient extends PurgomalumClient {

    public FakePurgomalumClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    public boolean containsProfanity(final String text) {
        return false;
    }
}
